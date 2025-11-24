package cn.net.yunlou.bole.request;

import cn.net.yunlou.bole.common.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: WorkExperienceAddRequest
 * Description:
 * Created By laughtiger
 * Created At 2025/11/25 00:08
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "新增工作经历请求")
public class WorkExperienceAddRequest extends BaseModel {
}
