package com.playkuround.playkuroundserver.domain.auth.email.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class AuthVerifyEmailResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;
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
