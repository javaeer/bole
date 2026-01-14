package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * FileName: ResumeTemplate Description: 简历模板 Created By MR. WANG Created At 2025/11/24 20:00
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "t_resumes_template", autoResultMap = true, resultMap = "BaseResultMap")
public class ResumesTemplate extends BaseEntity {

    /** 模板名称 */
    private String name;

    /** 模板编码 */
    private String code;

    /** 描述 */
    private String description;

    /** 预览图 */
    private String previewImage;

    /** 是否激活 */
    private Boolean isActive;

    /** 版本号 */
    private String version;

    /** 样式配置 */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private ResumesTemplateStyle globalStyle;

    /** 布局配置 */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private ResumesTemplateLayout globalLayout;

    // @TableField(typeHandler = JsonbTypeHandler.class)
    @TableField(exist = false)
    private List<ResumesTemplateComponent> components;
}
