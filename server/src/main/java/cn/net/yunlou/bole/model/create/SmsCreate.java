package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: SmsCreate Description: Created By laughtiger Created At 2025/12/25 01:13 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "短信请求")
@Builder
@AllArgsConstructor
public class SmsCreate extends BaseCreate {

    // @NotBlank(message = "区号不能为空")
    // @Schema(description = "区号", example = "86", requiredMode = Schema.RequiredMode.REQUIRED)
    // private String areaCode;

    @NotBlank(message = "手机号不能为空")
    @Schema(
            description = "手机号",
            example = "18610880038",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotNull(message = "模板不得为空")
    @Schema(description = "模板id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long templateId = 1L;
}
