package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseDTO;
import cn.net.yunlou.bole.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: UserResponse Description: Created By MR. WANG Created At 2025/11/19 13:48 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "登录响应体")
public class AccessTokenDTO extends BaseDTO {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private Long refreshExpiresIn;

    private User userInfo;
}
