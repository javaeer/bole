package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import java.util.List;
import java.util.Map;
import lombok.*;

/**
 * FileName: ResumeTemplate Description: 简历模板 Created By MR. WANG Created At 2025/11/24 20:00
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_resumes_template", resultMap = "resumesTemplateResultMap")
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
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> globalStyle;

    /** 布局配置 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> layout;

    @TableField(exist = false)
    private List<ResumesTemplateComponent> components;
}
