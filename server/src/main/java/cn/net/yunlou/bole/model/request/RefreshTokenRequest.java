package cn.net.yunlou.bole.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "密码", example = "eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoicmVmcmVzaCIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzYzOTc1MDczLCJleHAiOjE3NjQ1Nzk4NzN9.HlQs5TvpAYz5I18FAFYJTd09G3GVLjRKAx8Ad39Dtno", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}