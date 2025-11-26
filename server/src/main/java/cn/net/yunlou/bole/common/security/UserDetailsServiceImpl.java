package cn.net.yunlou.bole.common.security;

import cn.net.yunlou.bole.common.constant.BaseConstant;
import cn.net.yunlou.bole.entity.Role;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.entity.UserRole;
import cn.net.yunlou.bole.service.UserRoleService;
import cn.net.yunlou.bole.service.UserService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * FileName: UserDetailsServiceImpl
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/19 14:25
 * Modified By
 * Modified At
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {



    private final UserService userService;

    private final UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.findByUsername(username);

        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 查询用户角色
        UserRole query = new UserRole();
        query.setUserId(user.getId());
        List<Role> roles = userRoleService.listRight(query);

        // 转换为GrantedAuthority
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(BaseConstant.ROLE_PREFIX + role.getCode().toUpperCase()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(authorities)) {//默认普通用户
            authorities = Lists.newArrayList();
            authorities.add(new SimpleGrantedAuthority(BaseConstant.ROLE_PREFIX + "user".toUpperCase()));
        }
        user.setAuthorities(authorities);

        return new CustomUserDetails(user);
    }
}
