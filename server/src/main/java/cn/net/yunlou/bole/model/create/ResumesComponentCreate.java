package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import cn.net.yunlou.bole.model.entity.ResumesComponentDefaultConfig;
import lombok.*;

/**
 * FileName: ResumesComponentCreate Description: Created By laughtiger Created At 2025/12/17 16:22
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumesComponentCreate extends BaseCreate {

    private String name;

    /** 对应预定义组件的名称 */
    private String key;

    private ResumesComponentDefaultConfig defaultConfig;
}
