package cn.net.yunlou.bole.model.request;

import cn.net.yunlou.bole.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ResumesTemplateAddRequest Description: Created By MR. WANG Created At 2025/11/24 23:56
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "新增简历模板请求")
public class ResumesTemplateAddRequest extends BaseQuery {}
