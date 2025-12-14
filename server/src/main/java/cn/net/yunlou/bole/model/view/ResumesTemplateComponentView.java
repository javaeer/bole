package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ResumesTemplateComponentView Description: Created By laughtiger Created At 2025/12/13
 * 21:19 Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ResumesTemplateComponentView extends BaseView {

    private Long templateId;

    private String name;

    /** 对应预定义组件的名称 */
    private String component;

    /** 传递给组件的属性，根据正式数据，对其中的默认值进行替换 */
    private Map<String, Object> props;

    /** 该组件独有的样式变量（映射到CSS变量或类名）不可变 */
    private Map<String, Object> styles;
}
