package cn.net.yunlou.bole.common.security;

import cn.net.yunlou.bole.common.constant.BaseConstant;
import cn.net.yunlou.bole.common.constant.UserStatus;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.entity.UserRole;
import cn.net.yunlou.bole.service.UserRoleService;
import cn.net.yunlou.bole.service.UserService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * FileName: UserDetailsServiceImpl Description: Created By MR. WANG Created At 2025/11/19 14:25
 * Modified By Modified At
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UnifiedUserDetailsService implements UserDetailsService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final UserService userService;

    private final UserRoleService userRoleService;

    //@Override
    //public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //
    //    User user = userService.findByUsername(username);
    //
    //    if (user == null) {
    //        log.warn("用户不存在: {}", username);
    //        throw new UsernameNotFoundException("用户不存在: " + username);
    //    }
    //
    //    // 查询用户角色
    //    UserRole query = new UserRole();
    //    query.setUserId(user.getId());
    //    List<Role> roles = userRoleService.listRight(query);
    //
    //    // 转换为GrantedAuthority
    //    List<GrantedAuthority> authorities =
    //            roles.stream()
    //                    .map(
    //                            role ->
    //                                    new SimpleGrantedAuthority(
    //                                            BaseConstant.ROLE_PREFIX
    //                                                    + role.getCode().toUpperCase()))
    //                    .collect(Collectors.toList());
    //    if (CollectionUtils.isEmpty(authorities)) { // 默认普通用户
    //        authorities = Lists.newArrayList();
    //        authorities.add(
    //                new SimpleGrantedAuthority(BaseConstant.ROLE_PREFIX + "user".toUpperCase()));
    //    }
    //    user.setAuthorities(authorities);
    //
    //    return new CustomUserDetails(user);
    //}
    //


    /**
     * 统一用户加载方法
     * 支持用户名、手机号、邮箱自动识别
     */
    @Override
    public UnifiedUserDetails loadUserByUsername(String identifier) {
        log.debug("尝试加载用户: {}", identifier);


        // 1. 尝试按用户名查找
        User user = userService.findByUsername(identifier);

        // 2. 尝试按手机号查找
        if (user == null && isPhone(identifier)) {
            user = userService.findByPhone(identifier);
        }

        // 3. 尝试按邮箱查找
        if (user == null && isEmail(identifier)) {
            user = userService.findByEmail(identifier);
        }


        if (user == null) {
            log.warn("用户未找到: {}", identifier);
            throw new UsernameNotFoundException("用户不存在");
        }

        // 检查用户状态
        if (!UserStatus.isActive(user.getStatus())) {
            if (UserStatus.isLocked(user.getStatus())) {
                throw new LockedException("账户已被锁定");
            } else if (UserStatus.isDisabled(user.getStatus())) {
                throw new DisabledException("账户已被禁用");
            }
        }

        return buildUserDetails(user);
    }

    /**
     * 根据用户ID加载用户（用于JWT验证）
     */
    public UnifiedUserDetails loadUserById(Long userId) {
        log.debug("根据ID加载用户: {}", userId);

        User user = userService.getById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return buildUserDetails(user);
    }

    /**
     * 根据手机号加载用户
     */
    public UnifiedUserDetails loadUserByPhone(String phone) {
        User user = userService.findByPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("手机号未注册");
        }
        return buildUserDetails(user);
    }


    /**
     * 根据邮箱加载用户
     */
    public UnifiedUserDetails loadUserByEmail(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("邮箱未注册");
        }
        return buildUserDetails(user);
    }

    public UnifiedUserDetails loadUserByWechatOpenId(String wechatOpenId) {
        User user = userService.findByWechatOpenId(wechatOpenId);
        if (user == null) {
            throw new UsernameNotFoundException("微信未绑定");
        }
        return buildUserDetails(user);
    }


    private UnifiedUserDetails buildUserDetails(User user) {
        // 获取用户权限
        List<GrantedAuthority> authorities = getAuthorities(user.getId());
        if (CollectionUtils.isEmpty(authorities)) { // 默认普通用户
            authorities = Lists.newArrayList();
            authorities.add(
                    new SimpleGrantedAuthority(BaseConstant.ROLE_PREFIX + "user".toUpperCase()));
        }

        return UnifiedUserDetails.builder()
                .user(user)
                .uid(user.getId())// 使用唯一标识
                .username(user.getUsername())
                .password(user.getPassword())
                .enabled(UserStatus.isActive(user.getStatus()))
                .accountNonLocked(!UserStatus.isLocked(user.getStatus()))
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .authorities(authorities)
                .build();
    }

    private List<GrantedAuthority> getAuthorities(Long userId) {
        // 从数据库查询用户权限
        return userRoleService.listRight(UserRole.builder().userId(userId).build())
                .stream()
                .map(role -> new SimpleGrantedAuthority(
                        BaseConstant.ROLE_PREFIX
                                + role.getCode().toUpperCase()))
                .collect(Collectors.toList());
    }

    private boolean isPhone(String identifier) {
        return identifier != null && PHONE_PATTERN.matcher(identifier).matches();
    }

    private boolean isEmail(String identifier) {
        return identifier != null && EMAIL_PATTERN.matcher(identifier).matches();
    }


}
