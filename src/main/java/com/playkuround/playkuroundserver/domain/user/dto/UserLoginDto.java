package com.playkuround.playkuroundserver.domain.user.dto;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserLoginDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {

        private String grantType;
        private String accessToken;
        private String refreshToken;

        public static UserLoginDto.Response from(TokenDto tokenDto) {
            return UserLoginDto.Response.builder()
                    .grantType(tokenDto.getGrantType())
                    .accessToken(tokenDto.getAccessToken())
                    .refreshToken(tokenDto.getRefreshToken())
                    .build();
        }
    }
}
