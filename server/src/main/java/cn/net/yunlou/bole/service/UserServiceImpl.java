package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.mapper.UserMapper;
import cn.net.yunlou.bole.request.LoginRequest;
import cn.net.yunlou.bole.request.RegisterRequest;
import cn.net.yunlou.bole.response.UserResponse;
import org.springframework.stereotype.Service;

/**
 * FileName: UserServiceImpl Description: Created By laughtiger Created At 2025/11/19 13:49 Modified
 * By Modified At
 */
@Service
public class UserServiceImpl extends BaseService<UserMapper, User> implements IUserService {
    @Override
    public User findByUsername(String username) {
        return null;
    }

    @Override
    public UserResponse login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public UserResponse register(RegisterRequest registerRequest) {
        return null;
    }
}
