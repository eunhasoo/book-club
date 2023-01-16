package com.eunhasoo.bookclub.auth;

import com.eunhasoo.bookclub.exception.auth.UnAuthorizedAccessException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLongType = parameter.getParameterType().equals(Long.class);
        boolean hasCurrentUserAnnotation = parameter.hasParameterAnnotation(CurrentUser.class);

        return isLongType && hasCurrentUserAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String uri = request.getRequestURI();

        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            throw new UnAuthorizedAccessException(uri);
        }
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            throw new UnAuthorizedAccessException(uri);
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (userDetails == null || userDetails.getId() == null) {
            throw new UnAuthorizedAccessException(uri);
        }

        return userDetails.getId();
    }
}
