package cn.net.yunlou.bole.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "短信登录请求")
public class WechatLoginDTO {

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "微信授权码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jscode;

    @NotBlank(message = "完整用户信息的加密数据不能为空")
    @Schema(description = "完整用户信息的加密数据", example = "18610880038", requiredMode = Schema.RequiredMode.REQUIRED)
    private String encryptedData;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "微信加密算法的初始向量", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String iv;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "获取手机号码动态令牌", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String getPhoneCode;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "获取手机号码动态令牌", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String signature;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "获取手机号码动态令牌", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rawData;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "获取手机号码动态令牌", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userInfo;
}
