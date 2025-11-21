package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.request.LoginRequest;
import cn.net.yunlou.bole.request.RegisterRequest;
import cn.net.yunlou.bole.response.AccessTokenResponse;
import cn.net.yunlou.bole.response.RefreshTokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * FileName: AuthService
 * Description:
 * Created By laughtiger
 * Created At 2025/11/21 14:48
 * Modified By
 * Modified At
 */
public interface AuthService{


    AccessTokenResponse login(@Valid LoginRequest loginRequest);

    AccessTokenResponse register(@Valid RegisterRequest registerRequest);

    RefreshTokenResponse refreshToken(@Valid String refreshToken);

    void logout();
}
