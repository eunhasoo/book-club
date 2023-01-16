package com.eunhasoo.bookclub.config;

import com.eunhasoo.bookclub.auth.CustomUserDetailsService;
import com.eunhasoo.bookclub.auth.AuthenticationFailureHandler;
import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
import com.eunhasoo.bookclub.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationFailureHandler restAuthenticationFailureHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                    .disable()
                .formLogin()
                    .disable()
                .headers()
                .frameOptions()
                    .sameOrigin()
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(restAuthenticationFailureHandler)
                .and()
                .authorizeRequests()
                    .antMatchers(
                            "/h2-console/**",
                            "/favicon.ico",
                            "/**/*.png",
                            "/**/*.gif",
                            "/**/*.svg",
                            "/**/*.jpg",
                            "/**/*.html",
                            "/**/*.css",
                            "/**/*.js")
                        .permitAll()
                    .antMatchers(
                            "/api/bookInfo/**",
                            "/api/auth",
                            "/api/users",
                            "/api/reviews")
                        .permitAll()
                    .antMatchers(
                            "/api/books/**",
                            "/api/users/**",
                            "/api/bookshelves/**",
                            "/api/reviews/**")
                        .hasAnyAuthority(Role.USER.getKey(), Role.ADMIN.getKey())
                .anyRequest()
                    .authenticated()
                    .and()
                    .apply(new JwtSecurityConfig(tokenProvider, userDetailsService));

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}
