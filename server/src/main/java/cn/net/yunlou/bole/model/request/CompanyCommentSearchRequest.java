package cn.net.yunlou.bole.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: CompanyCommentSearchRequest Description: Created By MR. WANG Created At 2025/11/24
 * 23:22 Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "查询企业评论请求")
public class CompanyCommentSearchRequest extends BaseSearchRequest {}
