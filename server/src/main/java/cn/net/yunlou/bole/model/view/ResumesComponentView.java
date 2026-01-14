package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import cn.net.yunlou.bole.model.entity.ResumesComponentDefaultConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ResumesComponentView Description: Created By laughtiger Created At 2025/12/17 16:22
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumesComponentView extends BaseView {

    private String name;

    /** 对应预定义组件的名称 */
    private String key;

    private ResumesComponentDefaultConfig defaultConfig;
}
