package com.playkuround.playkuroundserver.domain.auth.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenReissueResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    public static TokenReissueResponse from(TokenDto tokenDto) {
        return TokenReissueResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}
