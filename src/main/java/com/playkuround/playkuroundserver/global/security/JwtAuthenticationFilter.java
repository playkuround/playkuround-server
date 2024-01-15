package com.playkuround.playkuroundserver.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.auth.token.domain.TokenType;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.ErrorResponse;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);
        if (accessToken != null) {
            if (isValidateAccessToken(accessToken)) {
                try {
                    Authentication authentication = tokenManager.authentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    jwtExceptionHandler(response, ErrorCode.FAIL_AUTHENTICATION);
                    return;
                }
            }
            else {
                jwtExceptionHandler(response, ErrorCode.INVALID_TOKEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(GrantType.BEARER.getType())) {
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

    private void jwtExceptionHandler(HttpServletResponse response, ErrorCode errorCode) {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            ErrorResponse errorResponse = ErrorResponse.of(errorCode);
            ApiResponse<Object> objectApiResponse = ApiResponse.create(false, null, errorResponse);
            String responseBody = objectMapper.writeValueAsString(objectApiResponse);
            response.getWriter().write(responseBody);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
