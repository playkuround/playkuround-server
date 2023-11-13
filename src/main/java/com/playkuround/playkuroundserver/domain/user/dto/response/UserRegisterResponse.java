package com.playkuround.playkuroundserver.domain.user.dto.response;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserRegisterResponse {

    @Schema(description = "Bearer 고정", example = "Bearer", requiredMode = RequiredMode.REQUIRED)
    private String grantType;
    @Schema(description = "Access Token", example = "eyJ0eXAiOiJ..{생략}..", requiredMode = RequiredMode.REQUIRED)
    private String accessToken;
    @Schema(description = "Refresh Token", example = "eyJ0aAdJ13..{생략}..", requiredMode = RequiredMode.REQUIRED)
    private String refreshToken;

    public static UserRegisterResponse from(TokenDto tokenDto) {
        return UserRegisterResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}
