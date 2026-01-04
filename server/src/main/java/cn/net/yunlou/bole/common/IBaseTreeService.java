package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** 树形服务接口 */
public interface IBaseTreeService<
                T extends BaseTreeEntity<T>,
                C extends BaseTreeCreate,
                V extends BaseTreeView<V>,
                E extends BaseTreeEdit,
                Q extends BaseTreeQuery>
        extends IBaseService<T, C, V, E, Q> {

    boolean removeNodesByPath(String path);

    // 获取直接子节点（带children）
    T getNodeWithChildren(Serializable id);

    // 获取所有子孙节点（完整子树）
    T getNodeWithSubTree(Serializable id);

    // 获取直接子节点列表
    List<T> listDirectChildren(Serializable id);

    List<V> listDirectChildrenView(Serializable id);

    // 获取直接子节点列表
    List<T> listDirectChildren(T entity);

    // 构建整棵树（从根节点开始）
    List<T> listWholeTree();

    // 构建指定节点的完整子树
    List<T> listDescendants(Serializable id);

    // 获取所有子节点列表（从指定实体开始）
    List<T> listDescendants(T entity);

    /** 获取叶子节点（最末梢节点） */
    List<T> listLeafNodes(Serializable id);

    /** 获取指定层级的节点 */
    List<T> listNodesByLevel(int level);

    /** 获取根节点下的所有一级子节点 */
    List<T> listRootDirectChildren();

    List<V> listRootDirectChildrenView();

    /** 分页查询子节点 */
    IPage<T> listDirectChildrenByPage(Serializable id, Page<T> page);

    /** 获取子树（分页加载） */
    IPage<T> listDescendantsByPage(Serializable id, int depth, Page<T> page);

    /** 懒加载子节点 */
    List<T> listDirectChildrenByPage(Serializable id, int page, int size);

    /** 批量根据父节点ID查询子节点 */
    Map<Long, List<T>> listDirectChildrenByParentIds(List<Long> parentIds);

    /** 批量移动节点到新的父节点 */
    boolean moveNodesToParent(List<Long> nodeIds, Long targetParentId);

    /** 根据路径查询节点 */
    T findByPath(String path);

    /** 获取父节点 */
    T findParentNode(Serializable id);

    /** 获取祖父节点 */
    T findGrandparentNode(Serializable id);

    /** 获取根节点 */
    T findRootNode(Serializable id);

    // 判断和统计

    /** 是否有子节点 */
    boolean hasChildNodes(Serializable id);

    /** 子节点数量 */
    long countDirectChildren(Serializable id);

    /** 判断节点是否是另一个节点的后代 */
    boolean isNodeDescendantOf(Long parentId, Long childId);
}
