package cn.net.yunlou.bole.response;

import cn.net.yunlou.bole.entity.User;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * FileName: UserResponse
 * Description:
 * Created By laughtiger
 * Created At 2025/11/19 13:48
 * Modified By
 * Modified At
 */
@Data
@Builder
public class AccessTokenResponse implements Serializable {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private Long refreshExpiresIn;

    private User userInfo;

    private List<String> permissions;
}
