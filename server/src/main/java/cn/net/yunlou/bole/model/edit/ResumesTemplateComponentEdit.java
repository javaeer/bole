package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.*;

/**
 * FileName: ResumesTemplateComponentEdit Description: Created By laughtiger Created At 2025/12/13
 * 21:20 Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumesTemplateComponentEdit extends BaseEdit {

    private Long templateId;

    private Long componentId;

    // =============================组件在当前模板中自定义内容 ======================

    /** 传递给组件的属性，根据正式数据，对其中的默认值进行替换 */
    @NotNull private Map<String, Object> props;

    /** 该组件独有的样式变量（映射到CSS变量或类名）不可变 */
    @NotNull private Map<String, Object> styles;

    // =============================组件在当前模板中自定义内容 ======================

}
