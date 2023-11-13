package com.playkuround.playkuroundserver.domain.auth.token.dto.response;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenReissueResponse {
    @Schema(description = "Bearer 고정", example = "Bearer")
    private String grantType;
    @Schema(description = "새로 발급된 Access Token", example = "eyJ0eXAiOiJ..{생략}..")
    private String accessToken;
    @Schema(description = "새로 발급된 Refresh Token", example = "eyJ0aAdJ13..{생략}..")
    private String refreshToken;

    public static TokenReissueResponse from(TokenDto tokenDto) {
        return TokenReissueResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}
