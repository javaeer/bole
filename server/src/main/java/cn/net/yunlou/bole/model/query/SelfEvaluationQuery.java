package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * FileName: SelfEvaluationQuery Description: Created By laughtiger Created At 2025/12/14 04:11
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SelfEvaluationQuery extends BaseQuery {

    private String content;

    //亮点
    private List<String> highlights;
}
