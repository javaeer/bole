package cn.net.yunlou.bole.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * FileName: RegisterRequest Description: Created By MR. WANG Created At 2025/11/19 14:23 Modified
 * By Modified At
 */
@Data
@Builder
@Schema(description = "注册请求实体")
public class RegisterRequest implements Serializable {

    @Schema(description = "用户名", example = "admin", requiredMode = Schema.RequiredMode.AUTO)
    private String username;

    @Schema(
            description = "邮箱",
            example = "admin@yunlou.net.cn",
            requiredMode = Schema.RequiredMode.AUTO)
    private String email;

    @Schema(description = "手机区号", example = "86", requiredMode = Schema.RequiredMode.AUTO)
    private String area;

    @Schema(description = "手机号", example = "18888888888", requiredMode = Schema.RequiredMode.AUTO)
    private String phone;

    @NotBlank(message = "密码不得为空")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
