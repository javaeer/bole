package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.model.AccessTokenDTO;
import cn.net.yunlou.bole.model.ChangePasswordDTO;
import cn.net.yunlou.bole.model.LoginDTO;
import cn.net.yunlou.bole.model.RefreshTokenViewDTO;
import cn.net.yunlou.bole.model.RegisterDTO;
import cn.net.yunlou.bole.model.ResetPasswordDTO;
import jakarta.validation.Valid;

/**
 * FileName: AuthService Description: Created By MR. WANG Created At 2025/11/21 14:48 Modified By
 * Modified At
 */
public interface AuthService {

    AccessTokenDTO login(@Valid LoginDTO loginDTO);

    AccessTokenDTO register(@Valid RegisterDTO registerDTO);

    RefreshTokenViewDTO refreshToken(@Valid String refreshToken);

    void logout();

    Boolean changePassword(@Valid ChangePasswordDTO request);

    Boolean resetPassword(@Valid ResetPasswordDTO request);
}
