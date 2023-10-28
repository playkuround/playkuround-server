package com.playkuround.playkuroundserver.domain.auth.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpiredAt;
    private Date refreshTokenExpiredAt;
}
