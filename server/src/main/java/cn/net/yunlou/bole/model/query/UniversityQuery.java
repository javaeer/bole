package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: UniversityCreate
 * Description:
 * Created By laughtiger
 * Created At 2025/12/30 17:38
 * Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UniversityQuery extends BaseQuery {

    private String code;

    private boolean followed;
}
