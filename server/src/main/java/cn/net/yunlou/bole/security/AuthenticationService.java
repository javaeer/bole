package cn.net.yunlou.bole.security;

import cn.net.yunlou.bole.constant.BaseConstant;
import cn.net.yunlou.bole.entity.Role;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.entity.UserRole;
import cn.net.yunlou.bole.response.AccessTokenResponse;
import cn.net.yunlou.bole.response.RefreshTokenResponse;
import cn.net.yunlou.bole.service.UserRoleService;
import cn.net.yunlou.bole.service.UserService;
import cn.net.yunlou.bole.utils.RedisCacheUtils;
import cn.net.yunlou.bole.utils.SecurityContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private static final String BLACKLIST_TOKEN_PREFIX = "blacklist:";

    private final UserService userService;

    private final UserRoleService userRoleService;

    private final UserDetailsService userDetailsService;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisCacheUtils redisCacheUtils;

    /**
     * 根据token获取认证信息
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String token, HttpServletRequest request) {
        try {
            String username = jwtTokenProvider.extractUsername(token);

            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 验证 token 与用户是否匹配
                if (jwtTokenProvider.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    return authentication;
                }
            }
        } catch (Exception e) {
            // 记录日志，但不抛出异常，让请求继续
            log.warn("Authentication failed for token: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 用户登录处理
     */
    public AccessTokenResponse login(User user) {

        // 更新最后登录时间
        userService.updateLastLoginTime(user.getId());

        // 生成 accessToken
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());


        // 从 UserDetails 获取权限
        //List<String> permissions = userDetails.getAuthorities().stream()
        //        .map(GrantedAuthority::getAuthority)
        //        .collect(Collectors.toList());

        List<String> permissions = getUserAuthorities(user.getId());

        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(BaseConstant.TOKEN_PREFIX.trim())
                .expiresIn(jwtTokenProvider.getAccessTokenRemainingTime(accessToken) / 1000)
                .userInfo(user)
                .permissions(permissions)
                .build();

    }

    /**
     * 获取用户权限列表
     */
    private List<String> getUserAuthorities(Long userId) {
        UserRole entity = new UserRole();
        entity.setUserId(userId);
        List<Role> roles = userRoleService.listRight(entity);

        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }

        // 将角色编码转换为带 "ROLE_" 前缀的权限字符串集合
        return roles.stream()
                .map(role -> {
                    String roleCode = role.getCode().toUpperCase();
                    return BaseConstant.ROLE_PREFIX + roleCode;
                })
                .collect(Collectors.toList());
    }


    /**
     * 刷新访问令牌
     */
    public RefreshTokenResponse refreshToken(String refreshToken) {
        try {
            // 验证刷新令牌
            if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
                throw new RuntimeException("无效的刷新令牌");
            }

            String username = jwtTokenProvider.extractUsername(refreshToken);

            // 检查刷新令牌是否在Redis中（可选，用于令牌撤销）
            if (!isRefreshTokenValid(username, refreshToken)) {
                throw new RuntimeException("刷新令牌已失效");
            }

            // 验证用户是否存在且有效
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 生成新的访问令牌
            String newAccessToken = jwtTokenProvider.generateAccessToken(username);

            // 可选：生成新的刷新令牌（滚动刷新）
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
            storeRefreshToken(username, newRefreshToken);

            return RefreshTokenResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken) // 返回新的刷新令牌
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getAccessTokenRemainingTime(newAccessToken) / 1000)
                    .refreshExpiresIn(jwtTokenProvider.getAccessTokenRemainingTime(newRefreshToken) / 1000)
                    .build();

        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            throw new RuntimeException("刷新令牌失败: " + e.getMessage());
        }
    }

    /**
     * 存储刷新令牌到Redis
     */
    private void storeRefreshToken(String username, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + username;
        // 存储刷新令牌，设置过期时间与令牌本身一致
        redisCacheUtils.putObject(key, refreshToken, jwtTokenProvider.getAccessTokenRemainingTime(refreshToken));
    }

    /**
     * 验证刷新令牌是否有效
     */
    private boolean isRefreshTokenValid(String username, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + username;
        String storedToken = redisCacheUtils.getObject(key, String.class);
        return refreshToken.equals(storedToken);
    }

    /**
     * 撤销令牌（登出时使用）
     */
    public void revokeTokens(String username) {
        // 删除刷新令牌
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + username;
        redisCacheUtils.delete(refreshTokenKey);

        // 可以将访问令牌加入黑名单（如果需要在过期前撤销）
        // String accessToken = ... // 需要从请求中获取
        // String blacklistKey = BLACKLIST_TOKEN_PREFIX + accessToken;
        // long remainingTime = jwtTokenProvider.getAccessTokenRemainingTime(accessToken);
        // if (remainingTime > 0) {
        //     redisCacheUtils.putObject(blacklistKey, "revoked",remainingTime);
        // }
    }


    public void logout() {
        String token = SecurityContextUtils.getCurrentToken();
        if (token != null) {
            String username = jwtTokenProvider.extractUsername(token);
            revokeTokens(username);
        }
    }
}