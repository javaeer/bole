package cn.net.yunlou.bole.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ResumesEditRequest
 * Description:
 * Created By laughtiger
 * Created At 2025/11/24 22:21
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "编辑简历请求")
public class ResumesEditRequest extends BaseEditRequest{
}
