package cn.net.yunlou.bole.common;

import java.io.Serializable;
import java.util.List;

/** 树形服务接口 */
public interface IBaseTreeService<
                T extends BaseTreeEntity<T>,
                C extends BaseTreeCreate,
                V extends BaseTreeView<V>,
                E extends BaseTreeEdit,
                Q extends BaseTreeQuery>
        extends IBaseService<T, C, V, E, Q> {

    // 获取直接子节点（带children）
    T getChildren(Serializable id);

    // 获取所有子孙节点（完整子树）
    T getAllChildren(Serializable id);

    // 获取直接子节点列表
    List<T> listChildren(Serializable id);

    /** 获取直接子节点列表 */
    List<T> listChildren(T entity);

    // 构建整棵树（从根节点开始）
    List<T> listAllChildren();

    // 构建指定节点的完整子树
    List<T> listAllChildren(Serializable id);

    /** 获取所有子节点列表（从指定实体开始） */
    List<T> listAllChildren(T entity);

    /** 获取叶子节点（最末梢节点） */
    List<T> listLeaf(Serializable id);

    /** 是否有子节点 */
    boolean hasChildren(Serializable id);

    /** 子节点数量 */
    long countChildren(Serializable id);

    /** 获取父节点 */
    T getParent(Serializable id);

    /** 获取祖父节点 */
    T getGrandpa(Serializable id);

    /** 获取根节点 */
    T getRoot(Serializable id);
}
