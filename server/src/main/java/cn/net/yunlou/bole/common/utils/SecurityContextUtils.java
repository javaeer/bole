package cn.net.yunlou.bole.common.utils;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.BaseConstant;
import cn.net.yunlou.bole.common.security.UnifiedUserDetails;
import cn.net.yunlou.bole.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
/** Spring Security 上下文工具类 提供当前用户信息、权限验证等便捷方法 */
public class SecurityContextUtils {

    private SecurityContextUtils() {
        log.debug("SecurityContextUtils initialized");
    }

    /**
     * 🔐 获取安全上下文
     */
    public static SecurityContext getContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * 👤 获取认证信息
     */
    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(getContext().getAuthentication());
    }

    /**
     * 🔄 设置认证信息
     *
     * @param authentication 认证对象
     */
    public static void setAuthentication(Authentication authentication) {
        getContext().setAuthentication(authentication);
    }

    /**
     * 👥 获取当前用户详细信息
     *
     * @return UserDetails对象，未认证返回empty
     */
    public static Optional<UserDetails> getCurrentUserDetails() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserDetails)
                .map(principal -> (UserDetails) principal);
    }

    /**
     * 📝 获取当前用户ID（需在 UserDetails 中实现 getUser().getId()）
     *
     * @return 当前登录用户的 ID
     * @throws BusinessException 如果用户未登录或身份信息无效
     */
    public static Long getCurrentUserId() {
        return getCurrentUserDetails()
                .filter(userDetails -> userDetails instanceof UnifiedUserDetails)
                .map(userDetails -> ((UnifiedUserDetails) userDetails).getUid())
                .orElseThrow(
                        () -> new BusinessException(BusinessStatus.UNAUTHORIZED_INVALID_EXPIRED));
    }

    public static User getCurrentUser() {
        return getCurrentUserDetails()
                .filter(userDetails -> userDetails instanceof UnifiedUserDetails)
                .map(userDetails -> ((UnifiedUserDetails) userDetails).getUser())
                .orElseThrow(
                        () -> new BusinessException(BusinessStatus.UNAUTHORIZED_INVALID_EXPIRED));
    }

    /**
     * 📋 获取用户权限集合
     *
     * @return 权限集合，未认证抛出异常
     */
    public static Set<String> getAuthorities() {
        return getAuthentication()
                .map(Authentication::getAuthorities)
                .filter(authorities -> !CollectionUtils.isEmpty(authorities))
                .map(
                        authorities ->
                                authorities.stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.toSet()))
                .orElseThrow(
                        () -> new BusinessException(BusinessStatus.UNAUTHORIZED_INVALID_EXPIRED));
    }

    /**
     * ✅ 检查用户是否已认证
     */
    public static boolean isAuthenticated() {
        return getAuthentication().map(Authentication::isAuthenticated).orElse(false);
    }

    /**
     * 🛡️ 检查是否拥有指定权限
     *
     * @param authority 权限标识
     */
    public static boolean hasAuthority(String authority) {
        if (authority == null || authority.trim().isEmpty()) {
            return false;
        }
        return getAuthorities().contains(authority);
    }

    /**
     * 🛡️ 检查是否拥有任意指定权限
     *
     * @param authorities 权限标识数组
     */
    public static boolean hasAnyAuthority(String... authorities) {
        if (authorities == null || authorities.length == 0) {
            return false;
        }

        Set<String> userAuthorities = getAuthorities();
        for (String authority : authorities) {
            if (userAuthorities.contains(authority)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 👑 检查是否拥有指定角色
     *
     * @param role 角色名称（自动添加ROLE_前缀）
     */
    public static boolean hasRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }

        String roleName =
                role.startsWith(BaseConstant.ROLE_PREFIX) ? role : BaseConstant.ROLE_PREFIX + role;
        return hasAuthority(roleName);
    }

    /**
     * 👑 检查是否拥有任意指定角色
     *
     * @param roles 角色名称数组
     */
    public static boolean hasAnyRole(String... roles) {
        if (roles == null) {
            return false;
        }

        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 🧹 清除安全上下文
     */
    public static void clearContext() {
        SecurityContextHolder.clearContext();
    }


}
