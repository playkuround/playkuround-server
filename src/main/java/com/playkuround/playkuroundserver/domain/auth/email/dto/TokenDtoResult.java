package com.playkuround.playkuroundserver.domain.auth.email.dto;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;

import java.util.Date;

public record TokenDtoResult(
        String grantType, String accessToken, String refreshToken,
        Date accessTokenExpiredAt, Date refreshTokenExpiredAt) implements AuthVerifyEmailResult {

    public TokenDtoResult(TokenDto tokenDto) {
        this(
                tokenDto.getGrantType(),
                tokenDto.getAccessToken(),
                tokenDto.getRefreshToken(),
                tokenDto.getAccessTokenExpiredAt(),
                tokenDto.getRefreshTokenExpiredAt()
        );
    }
}
