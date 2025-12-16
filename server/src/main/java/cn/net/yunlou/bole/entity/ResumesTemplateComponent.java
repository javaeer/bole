package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import java.util.Map;
import lombok.*;

/**
 * FileName: ComponentConfig Description: Created By laughtiger Created At 2025/12/13 02:26 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_resumes_template_component", resultMap = "resumesTemplateComponentResultMap")
public class ResumesTemplateComponent extends BaseEntity {

    private Long templateId;

    private String name;


    /**
     * 对应预定义组件的名称
     * @see cn.net.yunlou.bole.common.constant.TemplateComponentType
     */
    private String component;

    /** 传递给组件的属性，根据正式数据，对其中的默认值进行替换 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> props;

    /** 该组件独有的样式变量（映射到CSS变量或类名）不可变 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> styles;
}
