package cn.net.yunlou.bole.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ProjectExperienceSearchRequest
 * Description:
 * Created By laughtiger
 * Created At 2025/11/24 23:54
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "查询项目经历请求")
public class ProjectExperienceSearchRequest extends BaseSearchRequest{
}
