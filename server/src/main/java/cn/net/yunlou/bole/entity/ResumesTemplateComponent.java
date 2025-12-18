package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Map;
import lombok.*;

/**
 * FileName: ComponentConfig Description: Created By laughtiger Created At 2025/12/13 02:26 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(
        value = "t_resumes_template_component",
        autoResultMap = true,
        resultMap = "BaseResultMap")
public class ResumesTemplateComponent extends BaseEntity {

    private Long templateId;

    private Long componentId;

    // =========================来自组件的属性备份===================

    private String name;

    /**
     * 对应预定义组件的名称
     *
     * @see TemplateComponentKey
     */
    private String key;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private ResumesComponentDefaultConfig defaultConfig;

    // =========================来自组件的属性备份===================
    // =============================组件在当前模板中自定义内容 ======================
    /** 传递给组件的属性，根据正式数据，对其中的默认值进行替换 */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> props;

    /** 该组件独有的样式变量（映射到CSS变量或类名）不可变 */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> styles;

    // =============================组件在当前模板中自定义内容 ======================

}
