package cn.net.yunlou.bole.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: WorkExperienceSearchRequest
 * Description:
 * Created By laughtiger
 * Created At 2025/11/25 00:09
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "查询工作经历请求")
public class WorkExperienceSearchRequest extends BaseSearchRequest {
}
