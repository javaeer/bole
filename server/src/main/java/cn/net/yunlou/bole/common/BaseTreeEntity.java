package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

import lombok.*;

/**
 * FileName: BaseTreeEntity Description: 树形结构基础实体类 Created By MR. WANG Created At 2025/11/25 17:00
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseTreeEntity<T extends BaseTreeEntity<T>> extends BaseEntity {

    public static final Long ROOT_ID = 0L;

    /** 父节点ID */
    private Long parentId = ROOT_ID;

    /** 节点路径 */
    private String path;

    /** 层级 */
    private Integer level;

    /** 排序字段 */
    private Integer sort;

    /** 子节点列表 */
    @TableField(exist = false)
    @JsonManagedReference // 序列化时包含子对象
    private List<T> children;

    /** 父节点 */
    @TableField(exist = false)
    @JsonBackReference // 序列化时忽略父对象
    private T parent;
}
