package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenFindDao;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidTokenException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    @Value("${token.refresh-token-expiration}")
    private String refreshTokenTimeToLive;

    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenFindDao refreshTokenFindDao;

    public TokenDto.AccessTokenDto reissueAccessToken(String refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenDto)
                .orElseThrow(InvalidTokenException::new);
        String userEmail = refreshToken.getUserEmail();

        Date accessTokenExpiredAt = tokenManager.createAccessTokenExpirationTime();
        String accessToken = tokenManager.createAccessToken(userEmail, accessTokenExpiredAt);

        return TokenDto.AccessTokenDto.of(accessToken, accessTokenExpiredAt);
    }

    @Transactional
    public void registerRefreshToken(User user, String refreshTokenDto) {
        RefreshToken refreshToken = RefreshToken.of(
                user.getEmail(),
                refreshTokenDto,
                Integer.parseInt(refreshTokenTimeToLive)
        );
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void updateRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenFindDao.findByUser(user);
        refreshToken.updateTimeToLive(Integer.parseInt(refreshTokenTimeToLive));
    }

    @Transactional
    public void deleteRefreshTokenByUser(User user) {
        RefreshToken refreshToken = refreshTokenFindDao.findByUser(user);
        refreshTokenRepository.delete(refreshToken);
    }

}
