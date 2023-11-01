package com.playkuround.playkuroundserver.domain.auth.email.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import lombok.*;

public class AuthVerifyEmailDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Response {

        private String grantType;
        private String accessToken;
        private String refreshToken;
        private String authVerifyToken;

        public static AuthVerifyEmailDto.Response createByAuthVerifyToken(String authVerifyToken) {
            return AuthVerifyEmailDto.Response.builder()
                    .authVerifyToken(authVerifyToken)
                    .build();
        }

        public static AuthVerifyEmailDto.Response fromTokenDto(TokenDto tokenDto) {
            return AuthVerifyEmailDto.Response.builder()
                    .grantType(tokenDto.getGrantType())
                    .accessToken(tokenDto.getAccessToken())
                    .refreshToken(tokenDto.getRefreshToken())
                    .build();
        }
    }
}
