package com.playkuround.playkuroundserver.global.security;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.auth.token.domain.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);
        if (accessToken != null) {
            if (isValidateAccessToken(accessToken)) {
                setAuthenticationToContext(accessToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(GrantType.BEARER.name())) {
            return null;
        }
        return bearerToken.substring(7);
    }

    private boolean isValidateAccessToken(String accessToken) {
        if (!tokenManager.isValidateToken(accessToken)) {
            return false;
        }

        String tokenType = tokenManager.getTokenType(accessToken);
        if (!TokenType.ACCESS.name().equals(tokenType)) {
            return false;
        }

        return true;
    }

    private void setAuthenticationToContext(String accessToken) {
        Authentication authentication = tokenManager.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
