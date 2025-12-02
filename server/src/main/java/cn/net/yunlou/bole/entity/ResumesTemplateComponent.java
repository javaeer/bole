package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ResumesTemplateComponent Description: Created By MR. WANG Created At 2025/11/25 18:32
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_resumes_template_component")
public class ResumesTemplateComponent extends BaseEntity {

    private Long templateId;

    private Long componentId;

    private String componentType;

    private String componentName;

    private String componentConfig; // JSON格式

    private Integer sort;

    private Boolean isRequired;

    // 关联的组件库信息
    @TableField(exist = false)
    private ComponentLibrary componentLibrary;
}
