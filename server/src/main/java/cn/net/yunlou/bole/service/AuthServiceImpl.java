package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.constract.UserStatus;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.request.LoginRequest;
import cn.net.yunlou.bole.request.RegisterRequest;
import cn.net.yunlou.bole.response.UserResponse;
import cn.net.yunlou.bole.security.CustomUserDetails;
import cn.net.yunlou.bole.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse login(@Valid LoginRequest request) {
        try {
            // 使用 Spring Security 进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // 认证成功后，authentication 中已经包含用户信息
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 更新最后登录时间
            userService.updateLastLoginTime(user.getId());

            // 生成 token
            String token = jwtTokenProvider.generateToken(user.getUsername());

            // 从 UserDetails 获取权限
            List<String> permissions = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return UserResponse.builder()
                    .token(token)
                    .userInfo(user)
                    .permissions(permissions)
                    .build();

        } catch (BadCredentialsException e) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "用户名或密码错误");
        } catch (DisabledException e) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "账户已被禁用");
        } catch (LockedException e) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL, "账户已被锁定");
        }
    }

    @Override
    public UserResponse register(@Valid RegisterRequest registerRequest) {
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

        userService.save(user);

        // 为新用户生成 token（可选，根据业务需求决定是否让新用户自动登录）
        String token = jwtTokenProvider.generateToken(user.getUsername());

        // 新用户默认权限
        List<String> permissions = List.of("ROLE_USER");

        return UserResponse.builder()
                .token(token)
                .userInfo(user)
                .permissions(permissions)
                .build();
    }

}