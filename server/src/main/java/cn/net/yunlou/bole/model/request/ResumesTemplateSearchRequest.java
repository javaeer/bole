package cn.net.yunlou.bole.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ResumesTemplateSearchRequest Description: Created By MR. WANG Created At 2025/11/24
 * 23:56 Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "查询简历模板请求")
public class ResumesTemplateSearchRequest extends BaseSearchRequest {}
