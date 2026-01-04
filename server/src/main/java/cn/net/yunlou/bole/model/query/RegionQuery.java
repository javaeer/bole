package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseTreeQuery;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: RegionQuery Description: Created By laughtiger Created At 2026/1/2 23:11 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegionQuery extends BaseTreeQuery {

    @Parameter(description = "是否包含禁用数据")
    private Boolean includeDisabled = false;
}
