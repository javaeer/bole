package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.model.*;
import jakarta.validation.Valid;

/**
 * FileName: AuthService Description: Created By MR. WANG Created At 2025/11/21 14:48 Modified By
 * Modified At
 */
public interface AuthService {

    AccessTokenDTO login(@Valid LoginDTO loginDTO);

    AccessTokenDTO register(@Valid RegisterDTO registerDTO);

    AccessTokenDTO registerPhone(@Valid RegisterPhoneDTO request);

    AccessTokenDTO registerEmail(@Valid RegisterEmailDTO request);

    RefreshTokenViewDTO refreshToken(@Valid String refreshToken);

    void logout();

    Boolean changePassword(@Valid ChangePasswordDTO request);

    Boolean resetPassword(@Valid ResetPasswordDTO request);
}
