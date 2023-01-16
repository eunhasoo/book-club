package com.eunhasoo.bookclub.auth;

import com.eunhasoo.bookclub.common.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
public class AuthenticationFailureHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public AuthenticationFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = response.getWriter()) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .statusCode(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED))
                    .message(getMessage(authException))
                    .build();

            writer.println(objectMapper.writeValueAsString(errorResponse));
        }
    }

    private String getMessage(AuthenticationException authException) {
        String defaultMessage = "해당 요청에 대한 접근에 실패했습니다.";

        if (authException instanceof BadCredentialsException) {
            return "입력한 아이디 혹은 비밀번호가 올바르지 않습니다.";
        } else if (authException instanceof InsufficientAuthenticationException) {
            return "인증을 위해 필요한 정보가 부족합니다.";
        }

        return defaultMessage;
    }
}
