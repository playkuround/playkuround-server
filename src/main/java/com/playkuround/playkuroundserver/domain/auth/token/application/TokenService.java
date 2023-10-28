package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.AuthVerifyTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenFindDao;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.auth.token.exception.AuthVerifyTokenNotFoundException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    @Value("${token.refresh-token-expiration}")
    private String refreshTokenTimeToLive;

    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthVerifyTokenRepository authVerifyTokenRepository;

    @Transactional
    public void registerRefreshToken(Authentication authentication, String sRefreshToken) {
        refreshTokenRepository.findByUserEmail(authentication.getName())
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.of(
                authentication.getName(),
                sRefreshToken,
                Integer.parseInt(refreshTokenTimeToLive)
        );
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshTokenByUser(User user) {
        refreshTokenRepository.findByUserEmail(user.getEmail())
                .ifPresent(refreshTokenRepository::delete);
    }

    public AuthVerifyToken registerAuthVerifyToken() {
        AuthVerifyToken authVerifyToken = tokenManager.createAuthVerifyToken();
        return authVerifyTokenRepository.save(authVerifyToken);
    }

    public void validateAuthVerifyToken(String authVerifyToken) {
        if (!authVerifyTokenRepository.existsById(authVerifyToken)) {
            throw new AuthVerifyTokenNotFoundException();
        }
    }

    public void deleteAuthVerifyToken(String authVerifyToken) {
        authVerifyTokenRepository.findById(authVerifyToken)
                .ifPresent(authVerifyTokenRepository::delete);
    }
}
