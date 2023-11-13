package com.playkuround.playkuroundserver.domain.auth.email.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Schema(description = "이미 가입된 회원이면 grantType, accessToken, refreshToken이 반환됩니다. " +
        "신규 회원이면 authVerifyToken이 반환됩니다.")
public class AuthVerifyEmailResponse {
    @Schema(description = "Bearer 고정", example = "Bearer")
    private String grantType;
    @Schema(description = "Access Token", example = "eyJ0eXAiOiJ..{생략}..")
    private String accessToken;
    @Schema(description = "Refresh Token. 기존의 저장된 refreshToken이 있다면 이 토큰으로 교체해야 합니다.", example = "eyJ0aAdJ13..{생략}..")
    private String refreshToken;
    @Schema(description = "인증 토큰. 회원가입 시 필요합니다. 유효기간은 5분입니다.", example = "dfewefasdfwe..{생략}..")
    private String authVerifyToken;

    public static AuthVerifyEmailResponse createByAuthVerifyToken(String authVerifyToken) {
        return AuthVerifyEmailResponse.builder()
                .authVerifyToken(authVerifyToken)
                .build();
    }

    public static AuthVerifyEmailResponse fromTokenDto(TokenDto tokenDto) {
        return AuthVerifyEmailResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}
