package com.playkuround.playkuroundserver.domain.auth.email.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import lombok.*;

import java.util.Date;

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

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date accessTokenExpiredAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date refreshTokenExpiredAt;

        public static AuthVerifyEmailDto.Response createByAuthVerifyToken(String authVerifyToken) {
            return AuthVerifyEmailDto.Response.builder()
                    .authVerifyToken(authVerifyToken)
                    .build();
        }

        public static AuthVerifyEmailDto.Response from(TokenDto tokenDto) {
            return AuthVerifyEmailDto.Response.builder()
                    .grantType(tokenDto.getGrantType())
                    .accessToken(tokenDto.getAccessToken())
                    .accessTokenExpiredAt(tokenDto.getAccessTokenExpiredAt())
                    .refreshToken(tokenDto.getRefreshToken())
                    .refreshTokenExpiredAt(tokenDto.getRefreshTokenExpiredAt())
                    .build();
        }
    }
}
