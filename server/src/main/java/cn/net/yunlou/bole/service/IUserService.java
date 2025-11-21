package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.request.LoginRequest;
import cn.net.yunlou.bole.request.RegisterRequest;
import cn.net.yunlou.bole.response.UserResponse;
import jakarta.validation.Valid;

/**
 * FileName: IUserService Description: Created By laughtiger Created At 2025/11/19 13:48 Modified By
 * Modified At
 */
public interface IUserService extends IBaseService<User> {
    User findByUsername(String username);

    UserResponse login(@Valid LoginRequest loginRequest);

    UserResponse register(RegisterRequest registerRequest);
}
