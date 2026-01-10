package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: DocumentTaskQuery
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 21:55
 * Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentTaskQuery extends BaseQuery {

    private Long resumesId;
}
