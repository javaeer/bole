package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: RegisterRequest Description: Created By MR. WANG Created At 2025/11/19 14:23 Modified
 * By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "邮箱注册请求实体")
public class RegisterEmailDTO extends BaseDTO {

    @NotBlank(message = "邮件不得为空")
    @Schema(
            description = "邮箱",
            example = "admin@yunlou.net.cn",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;


    @NotBlank(message = "密码不得为空")
    @Schema(
            description = "密码",
            example = "123456",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "验证码不得为空")
    @Schema(description = "验证码", example = "6666", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
}
