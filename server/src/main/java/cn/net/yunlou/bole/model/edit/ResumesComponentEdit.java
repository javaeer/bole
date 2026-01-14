package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import cn.net.yunlou.bole.model.entity.ResumesComponentDefaultConfig;
import lombok.*;

/**
 * FileName: ResumesComponentEdit Description: Created By laughtiger Created At 2025/12/17 16:22
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumesComponentEdit extends BaseEdit {

    private String name;

    /** 对应预定义组件的名称 */
    private String key;

    private ResumesComponentDefaultConfig defaultConfig;
}
