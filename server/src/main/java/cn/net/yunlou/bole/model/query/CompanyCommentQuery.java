package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * FileName: CompanyCommentSearchRequest Description: Created By MR. WANG Created At 2025/11/24
 * 23:22 Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "查询企业评论请求")
public class CompanyCommentQuery extends BaseQuery {
    private Long companyId;
}
