package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Schema(description = "刷新token响应体")
public class RefreshTokenViewDTO extends BaseDTO {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private Long refreshExpiresIn;
}
