package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenIssueService {

    private final TokenManager tokenManager;

    public TokenDto.AccessTokenDto reissueAccessToken(String refreshToken) {
        String userEmail = tokenManager.getUserEmail(refreshToken);
        Date accessTokenExpiredAt = tokenManager.createAccessTokenExpirationTime();
        String accessToken = tokenManager.createAccessToken(userEmail, accessTokenExpiredAt);

        return TokenDto.AccessTokenDto.of(accessToken, accessTokenExpiredAt);
    }

}
