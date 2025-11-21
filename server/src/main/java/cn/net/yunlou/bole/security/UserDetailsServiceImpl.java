package cn.net.yunlou.bole.security;

import cn.net.yunlou.bole.constract.UserStatus;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * FileName: UserDetailsServiceImpl
 * Description:
 * Created By laughtiger
 * Created At 2025/11/19 14:25
 * Modified By
 * Modified At
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.findByUsername(username);

        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (!UserStatus.ACTIVE.getValue().equals(user.getStatus())) {
            log.warn("用户状态异常: {}, 状态: {}", username, user.getStatus());
            throw new RuntimeException("用户状态异常");
        }

        return new CustomUserDetails(user);
    }
}
