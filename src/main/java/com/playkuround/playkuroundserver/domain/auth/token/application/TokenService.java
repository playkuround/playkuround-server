package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    @Value("${token.refresh-token-expiration}")
    private String refreshTokenTimeToLive;

    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenDto.AccessTokenDto reissueAccessToken(String refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenDto)
                .orElseThrow(InvalidTokenException::new);
        String userEmail = refreshToken.getUserEmail();

        Date accessTokenExpiredAt = tokenManager.createAccessTokenExpirationTime();
        String accessToken = tokenManager.createAccessToken(userEmail, accessTokenExpiredAt);

        return TokenDto.AccessTokenDto.of(accessToken, accessTokenExpiredAt);
    }

    @Transactional
    public void registerRefreshToken(String userEmail, String refreshTokenDto) {
        RefreshToken refreshToken = RefreshToken.of(
                userEmail,
                refreshTokenDto,
                Integer.parseInt(refreshTokenTimeToLive)
        );
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshTokenByUserEmail(String userEmail) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserEmail(userEmail);
        refreshTokens.forEach(refreshToken -> refreshTokenRepository.deleteById(refreshToken.getRefreshToken()));
    }

}
