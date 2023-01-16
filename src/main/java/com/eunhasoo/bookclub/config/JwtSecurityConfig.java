package com.eunhasoo.bookclub.config;

import com.eunhasoo.bookclub.auth.CustomUserDetailsService;
import com.eunhasoo.bookclub.auth.jwt.JwtFilter;
import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtSecurityConfig(TokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        builder.addFilterBefore(
                new JwtFilter(tokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
