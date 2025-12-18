package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import lombok.*;

/**
 * FileName: ResumesTemplateComponentQuery Description: Created By laughtiger Created At 2025/12/13
 * 21:20 Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumesTemplateComponentQuery extends BaseQuery {

    private Long templateId;
}
