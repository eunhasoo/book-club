package com.eunhasoo.bookclub.auth;

import com.eunhasoo.bookclub.user.domain.Role;
import com.eunhasoo.bookclub.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private Collection<? extends GrantedAuthority> authorities;

    private CustomUserDetails(Long id,
                              String username,
                              String password,
                              String nickname,
                              Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.authorities = authorities;
    }

    public static CustomUserDetails create(User user) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(Role.USER.getKey()));
        return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), user.getNickname(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
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
