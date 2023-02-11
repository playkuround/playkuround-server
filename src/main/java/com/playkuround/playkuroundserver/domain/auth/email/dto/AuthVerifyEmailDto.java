package com.playkuround.playkuroundserver.domain.auth.email.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import lombok.*;

import java.util.Date;

public class AuthVerifyEmailDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Response {

        private String grantType;

        private String accessToken;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date accessTokenExpiredAt;

        private String refreshToken;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date refreshTokenExpiredAt;

        public static AuthVerifyEmailDto.Response of(TokenDto tokenDto) {
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
