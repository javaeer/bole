package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "刷新令牌请求")
public class RefreshTokenDTO extends BaseDTO {
    @NotBlank(message = "刷新令牌不能为空")
    @Schema(
            description = "密码",
            example =
                    "eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoicmVmcmVzaCIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzYzOTc1MDczLCJleHAiOjE3NjQ1Nzk4NzN9.HlQs5TvpAYz5I18FAFYJTd09G3GVLjRKAx8Ad39Dtno",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}
