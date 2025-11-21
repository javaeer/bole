package cn.net.yunlou.bole.security;

import cn.net.yunlou.bole.constract.UserStatus;
import cn.net.yunlou.bole.entity.Role;
import cn.net.yunlou.bole.entity.User;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    @Getter
    private final User user;

    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;

        ArrayList<@Nullable Role> roles = Lists.newArrayList();
        Role role1 = new Role();
        role1.setName("ADMIN");
        roles.add(role1);
        this.authorities = roles.stream().filter(Objects::nonNull)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Objects.equals(user.getStatus(), UserStatus.BANNED.getValue());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}