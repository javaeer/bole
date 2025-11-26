package cn.net.yunlou.bole.common;

import java.io.Serializable;
import java.util.List;

/**
 * 树形服务接口
 */
public interface IBaseTreeService<T, D, Q> extends IBaseService<T, D, Q> {
    
    /**
     * 获取子节点（包含直接子节点）
     */
    T getChildren(Serializable id);
    
    /**
     * 获取所有子节点（递归）
     */
    T getAllChildren(Serializable id);
    
    /**
     * 获取直接子节点列表
     */
    List<T> listChildren(Serializable id);
    
    /**
     * 获取直接子节点列表
     */
    List<T> listChildren(T entity);
    
    /**
     * 获取所有子节点列表（从根开始）
     */
    List<T> listAllChildren();
    
    /**
     * 获取所有子节点列表（从指定节点开始）
     */
    List<T> listAllChildren(Serializable id);
    
    /**
     * 获取所有子节点列表（从指定实体开始）
     */
    List<T> listAllChildren(T entity);
    
    /**
     * 获取叶子节点列表
     */
    List<T> listLeaf(Serializable id);
    
    /**
     * 是否有子节点
     */
    boolean hasChildren(Serializable id);
    
    /**
     * 子节点数量
     */
    long countChildren(Serializable id);
    
    /**
     * 获取父节点
     */
    T getParent(Serializable id);
    
    /**
     * 获取祖父节点
     */
    T getGrandpa(Serializable id);
    
    /**
     * 获取根节点
     */
    T getRoot(Serializable id);
}