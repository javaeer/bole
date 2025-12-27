package cn.net.yunlou.bole.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "短信登录请求")
public class SmsLoginDTO {

    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号", example = "18610880038", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
}
