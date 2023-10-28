package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.auth.token.domain.TokenType;
import com.playkuround.playkuroundserver.global.error.exception.AuthenticationException;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RefreshTokenValidator {

    private final TokenManager tokenManager;

    public void validateRefreshToken(String authorizationHeader) {
        // 토큰 유무 확인
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new AuthenticationException(ErrorCode.EMPTY_AUTHORIZATION);
        }

        // GrantType 이 Bearer 인지 검증
        String[] authorizations = authorizationHeader.split(" ");
        if (authorizations.length < 2 || (!GrantType.BEARER.name().equals(authorizations[0].toUpperCase()))) {
            throw new AuthenticationException(ErrorCode.NOT_BEARER_GRANT_TYPE);
        }

        // 토큰 유효성 검증
        String refreshToken = authorizations[1];
        if (!tokenManager.isValidateToken(refreshToken)) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }

        // 토큰 타입이 REFRESH 인지 검증
        String tokenType = tokenManager.getTokenType(refreshToken);
        if (!TokenType.REFRESH.name().equals(tokenType)) {
            throw new AuthenticationException(ErrorCode.NOT_REFRESH_TOKEN_TYPE);
        }
    }

}
