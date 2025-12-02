package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.model.request.ChangePasswordRequest;
import cn.net.yunlou.bole.model.request.LoginRequest;
import cn.net.yunlou.bole.model.request.RegisterRequest;
import cn.net.yunlou.bole.model.request.ResetPasswordRequest;
import cn.net.yunlou.bole.model.response.AccessTokenResponse;
import cn.net.yunlou.bole.model.response.RefreshTokenResponse;
import jakarta.validation.Valid;

/**
 * FileName: AuthService Description: Created By MR. WANG Created At 2025/11/21 14:48 Modified By
 * Modified At
 */
public interface AuthService {

    AccessTokenResponse login(@Valid LoginRequest loginRequest);

    AccessTokenResponse register(@Valid RegisterRequest registerRequest);

    RefreshTokenResponse refreshToken(@Valid String refreshToken);

    void logout();

    Boolean changePassword(@Valid ChangePasswordRequest request);

    Boolean resetPassword(@Valid ResetPasswordRequest request);
}
