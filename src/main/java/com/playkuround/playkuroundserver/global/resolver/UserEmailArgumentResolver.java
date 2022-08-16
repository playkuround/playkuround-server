package com.playkuround.playkuroundserver.global.resolver;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class UserEmailArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenManager tokenManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasEmailAnnotation = parameter.hasParameterAnnotation(UserEmail.class);
        boolean hasString = String.class.isAssignableFrom(parameter.getParameterType());

        return hasEmailAnnotation && hasString;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        token = token.split(" ")[1];
        String userEmail = tokenManager.getUserEmail(token);
        System.out.println("userEmail="+userEmail);
        return userEmail;
    }

}
