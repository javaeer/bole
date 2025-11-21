package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.constant.UserStatus;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.request.LoginRequest;
import cn.net.yunlou.bole.request.RegisterRequest;
import cn.net.yunlou.bole.response.AccessTokenResponse;
import cn.net.yunlou.bole.response.RefreshTokenResponse;
import cn.net.yunlou.bole.security.AuthenticationService;
import cn.net.yunlou.bole.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationService authenticationService;

    private final AuthenticationManager authenticationManager;

    @Override
    public AccessTokenResponse login(@Valid LoginRequest request) {

        try {
            // 使用 Spring Security 进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

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
    public AccessTokenResponse register(@Valid RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(registerRequest.getUsername())) {
            throw new BusinessException(BusinessStatus.ALREADY_EXISTS, "用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userService.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException(BusinessStatus.ALREADY_EXISTS, "邮箱已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
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
    public RefreshTokenResponse refreshToken(String refreshToken) {
        return authenticationService.refreshToken(refreshToken);
    }

    @Override
    public void logout() {
        //String token = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));
        //if (token != null) {
        //    String username = jwtTokenProvider.extractUsername(token);
        //    authService.revokeTokens(username);
        //}
        authenticationService.logout();
    }
}