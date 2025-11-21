package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.request.LoginRequest;
import cn.net.yunlou.bole.request.RegisterRequest;
import cn.net.yunlou.bole.response.UserResponse;
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


    UserResponse login(@Valid LoginRequest loginRequest);


    UserResponse register(@Valid RegisterRequest registerRequest);
}
