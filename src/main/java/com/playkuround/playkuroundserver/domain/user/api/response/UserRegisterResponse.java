package com.playkuround.playkuroundserver.domain.user.api.response;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserRegisterResponse {

    @Schema(description = "Bearer 고정", example = "Bearer", requiredMode = RequiredMode.REQUIRED)
    private String grantType;

    @Schema(description = "Access Token", example = "eyJ0eXAiOiJ..{생략}..", requiredMode = RequiredMode.REQUIRED)
    private String accessToken;

    @Schema(description = "Refresh Token", example = "eyJ0aAdJ13..{생략}..", requiredMode = RequiredMode.REQUIRED)
    private String refreshToken;

    public static UserRegisterResponse from(TokenDto tokenDto) {
        return new UserRegisterResponse(tokenDto.getGrantType(), tokenDto.getAccessToken(), tokenDto.getRefreshToken());
    }
}
