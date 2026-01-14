package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: CompanyQuery Description: Created By MR. WANG Created At 2025/11/26 17:43 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyQuery extends BaseQuery {

    private boolean followed;
}
