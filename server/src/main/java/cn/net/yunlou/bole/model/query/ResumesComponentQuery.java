package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import lombok.*;

/**
 * FileName: ResumesComponentQuery Description: Created By laughtiger Created At 2025/12/17 16:23
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumesComponentQuery extends BaseQuery {

    private String name;

    /** 对应预定义组件的名称 */
    private String key;
}
