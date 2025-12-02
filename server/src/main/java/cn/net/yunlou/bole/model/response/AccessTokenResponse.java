package cn.net.yunlou.bole.model.response;

import cn.net.yunlou.bole.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FileName: UserResponse Description: Created By MR. WANG Created At 2025/11/19 13:48 Modified By
 * Modified At
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "登录响应体")
public class AccessTokenResponse implements Serializable {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private Long refreshExpiresIn;

    private User userInfo;
}
