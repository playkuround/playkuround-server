package com.playkuround.playkuroundserver.domain.user.dto.response;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserRegisterResponse {

    private String grantType;
    private String accessToken;
    private String refreshToken;

    public static UserRegisterResponse from(TokenDto tokenDto) {
        return UserRegisterResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}
