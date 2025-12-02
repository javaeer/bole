package cn.net.yunlou.bole.common.utils;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.BaseTreeEntity;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

/**
 * 树形结构构建工具类
 *
 * <p>public List<Department> getDepartmentTree() { // 从数据库获取所有部门 List<Department> allDepartments =
 * departmentMapper.selectList(null);
 *
 * <p>// 构建树形结构 return TreeBuildUtils.buildFullTree(allDepartments); }
 *
 * <p>public List<Department> getDepartmentTreeByParent(Long parentId) { List<Department>
 * allDepartments = departmentMapper.selectList(null);
 *
 * <p>// 按sort字段排序 return TreeBuildUtils.buildTree(allDepartments, parentId,
 * Comparator.comparing(Department::getSort)); }
 *
 * <p>public List<Department> getDepartmentTreeCustomSort() { List<Department> allDepartments =
 * departmentMapper.selectList(null);
 *
 * <p>// 自定义排序：先按sort，再按name Comparator<Department> comparator = Comparator
 * .comparing(Department::getSort) .thenComparing(Department::getName);
 *
 * <p>return TreeBuildUtils.buildTree(allDepartments, BaseTreeEntity.ROOT_ID, comparator); }
 */
public class TreeBuildUtils {

    private TreeBuildUtils() {
        // 工具类，防止实例化
    }

    /**
     * 构建树形结构（基础版本）
     *
     * @param allNodes 所有节点列表
     * @param rootId 根节点ID
     * @param <T> 节点类型
     * @return 树形结构列表
     */
    public static <T extends BaseTreeEntity<T>> List<T> buildTree(List<T> allNodes, Long rootId) {
        return buildTree(allNodes, rootId, null);
    }

    /**
     * 构建树形结构（带排序）
     *
     * @param allNodes 所有节点列表
     * @param rootId 根节点ID
     * @param comparator 排序比较器
     * @param <T> 节点类型
     * @return 树形结构列表
     */
    public static <T extends BaseTreeEntity<T>> List<T> buildTree(
            List<T> allNodes, Long rootId, Comparator<T> comparator) {
        // 参数校验
        if (CollectionUtils.isEmpty(allNodes)) {
            return new ArrayList<>();
        }

        // 检测循环引用
        checkCircularReference(allNodes);

        // 构建ID到节点的映射
        Map<Long, T> nodeMap =
                allNodes.stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        // 按父ID分组
        Map<Long, List<T>> childrenMap =
                allNodes.stream()
                        .filter(node -> node.getParentId() != null)
                        .collect(
                                Collectors.groupingBy(
                                        T::getParentId, Collectors.toCollection(ArrayList::new)));

        // 设置子节点和父节点关系
        allNodes.forEach(
                node -> {
                    // 设置子节点
                    List<T> children = childrenMap.getOrDefault(node.getId(), new ArrayList<>());

                    // 排序子节点
                    if (comparator != null) {
                        children.sort(comparator);
                    } else {
                        // 默认按sort字段排序
                        children.sort(Comparator.comparing(T::getSort));
                    }

                    node.setChildren(children);

                    // 设置父节点
                    Long parentId = node.getParentId();
                    if (parentId != null && !parentId.equals(BaseTreeEntity.ROOT_ID)) {
                        T parent = nodeMap.get(parentId);
                        node.setParent(parent);
                    }
                });

        // 返回根节点下的子节点
        return childrenMap.getOrDefault(rootId, new ArrayList<>());
    }

    /**
     * 构建完整的树形结构（从根节点开始）
     *
     * @param allNodes 所有节点列表
     * @param <T> 节点类型
     * @return 完整的树形结构
     */
    public static <T extends BaseTreeEntity<T>> List<T> buildFullTree(List<T> allNodes) {
        return buildTree(allNodes, BaseTreeEntity.ROOT_ID);
    }

    /** 检测循环引用 */
    private static <T extends BaseTreeEntity<T>> void checkCircularReference(List<T> allNodes) {
        Map<Long, T> nodeMap =
                allNodes.stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        Set<Long> visited = new HashSet<>();

        for (T node : allNodes) {
            if (!visited.contains(node.getId())) {
                Set<Long> path = new HashSet<>();
                if (hasCircularReference(node, nodeMap, path)) {
                    throw new IllegalArgumentException("检测到循环引用，节点ID: " + node.getId());
                }
                visited.addAll(path);
            }
        }
    }

    /** 递归检测循环引用 */
    private static <T extends BaseTreeEntity<T>> boolean hasCircularReference(
            T node, Map<Long, T> nodeMap, Set<Long> path) {
        Long nodeId = node.getId();

        // 如果当前节点已经在路径中，说明存在循环引用
        if (path.contains(nodeId)) {
            return true;
        }

        path.add(nodeId);

        // 检查父节点链
        Long parentId = node.getParentId();
        if (parentId != null
                && !parentId.equals(BaseTreeEntity.ROOT_ID)
                && nodeMap.containsKey(parentId)) {
            T parent = nodeMap.get(parentId);
            if (hasCircularReference(parent, nodeMap, path)) {
                return true;
            }
        }

        path.remove(nodeId);
        return false;
    }

    /**
     * 扁平化树形结构
     *
     * @param tree 树形结构
     * @param <T> 节点类型
     * @return 扁平化的节点列表
     */
    public static <T extends BaseTreeEntity<T>> List<T> flattenTree(List<T> tree) {
        List<T> result = new ArrayList<>();
        flattenTreeRecursive(tree, result);
        return result;
    }

    /** 递归扁平化树形结构 */
    private static <T extends BaseTreeEntity<T>> void flattenTreeRecursive(
            List<T> nodes, List<T> result) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }

        for (T node : nodes) {
            result.add(node);
            flattenTreeRecursive(node.getChildren(), result);
        }
    }

    /**
     * 查找节点路径
     *
     * @param allNodes 所有节点
     * @param nodeId 目标节点ID
     * @param <T> 节点类型
     * @return 从根节点到目标节点的路径
     */
    public static <T extends BaseTreeEntity<T>> List<T> findNodePath(
            List<T> allNodes, Long nodeId) {
        Map<Long, T> nodeMap =
                allNodes.stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        List<T> path = new ArrayList<>();
        T currentNode = nodeMap.get(nodeId);

        while (currentNode != null) {
            path.add(0, currentNode); // 添加到开头，保证顺序从根节点到当前节点
            Long parentId = currentNode.getParentId();
            if (parentId == null || parentId.equals(BaseTreeEntity.ROOT_ID)) {
                break;
            }
            currentNode = nodeMap.get(parentId);
        }

        return path;
    }
}
