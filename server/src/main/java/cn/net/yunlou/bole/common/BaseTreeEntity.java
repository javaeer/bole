package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: BaseTreeEntity
 * Description: 树形结构基础实体类
 * Created By MR. WANG
 * Created At 2025/11/25 17:00
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTreeEntity<T extends BaseTreeEntity<T>> extends BaseEntity {

    public static final Long ROOT_ID = 0L;

    /**
     * 父节点ID
     */
    private Long parentId = ROOT_ID;

    /**
     * 节点路径
     */
    private String path;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 子节点列表
     */
    @TableField(exist = false)
    private List<T> children;

    /**
     * 父节点
     */
    @TableField(exist = false)
    private T parent;

    /**
     * 获取子节点列表，如果为null则返回空列表
     */
    public List<T> getChildren() {
        return children != null ? children : new ArrayList<>();
    }

    /**
     * 设置子节点列表
     */
    public void setChildren(List<T> children) {
        this.children = children;
    }

    /**
     * 判断是否为根节点
     */
    public boolean isRoot() {
        return ROOT_ID.equals(parentId);
    }

    /**
     * 添加子节点
     */
    public void addChild(T child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
        child.setParent((T) this);
    }
}