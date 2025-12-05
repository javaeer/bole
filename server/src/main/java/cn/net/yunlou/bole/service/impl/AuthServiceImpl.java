package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.UserStatus;
import cn.net.yunlou.bole.common.security.AuthenticationService;
import cn.net.yunlou.bole.common.security.CustomUserDetails;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.model.*;
import cn.net.yunlou.bole.service.AuthService;
import cn.net.yunlou.bole.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationService authenticationService;

    private final AuthenticationManager authenticationManager;

    @Override
    public AccessTokenDTO login(@Valid LoginDTO request) {

        try {
            // 使用 Spring Security 进行认证
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(), request.getPassword()));

            // 认证成功后，authentication 中已经包含用户信息
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            User user = userDetails.getUser();

            return authenticationService.login(user);

        } catch (BadCredentialsException e) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "用户名或密码错误");
        } catch (DisabledException e) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "账户已被禁用");
        } catch (LockedException e) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "账户已被锁定");
        }
    }

    @Override
    public AccessTokenDTO register(@Valid RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(registerDTO.getUsername())) {
            throw new BusinessException(BusinessStatus.ALREADY_EXISTS, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userService.existsByEmail(registerDTO.getEmail())) {
            throw new BusinessException(BusinessStatus.ALREADY_EXISTS, "邮箱已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(UserStatus.ACTIVE.getValue());
        user.setFollowers(0);
        user.setFans(0);
        user.setLikes(0);

        if (!userService.save(user)) {
            throw new BusinessException(BusinessStatus.GONE_DATA_INVALID, "注册失败,请稍后");
        }

        return authenticationService.login(user);
    }

    @Override
    public RefreshTokenViewDTO refreshToken(String refreshToken) {
        return authenticationService.refreshToken(refreshToken);
    }

    @Override
    public void logout() {
        authenticationService.logout();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(@Valid ChangePasswordDTO request) {

        // (SecurityContextUtils.getCurrentUserId()).build();

        return false;
    }

    @Override
    public Boolean resetPassword(@Valid ResetPasswordDTO request) {

        // 1.验证短信、邮箱验证码

        // 2.重置密码 到用户新密码

        return null;
    }
}
