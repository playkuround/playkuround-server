package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.AuthVerifyTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.auth.token.exception.AuthVerifyTokenNotFoundException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthVerifyTokenRepository authVerifyTokenRepository;

    public void saveRefreshToken(String username, String sRefreshToken) {
        refreshTokenRepository.deleteByUserEmail(username);

        RefreshToken refreshToken = tokenManager.createRefreshTokenEntity(username, sRefreshToken);
        refreshTokenRepository.save(refreshToken);
    }

    public AuthVerifyToken saveAuthVerifyToken() {
        AuthVerifyToken authVerifyToken = tokenManager.createAuthVerifyTokenEntity();
        return authVerifyTokenRepository.save(authVerifyToken);
    }

    public void deleteRefreshTokenByUser(User user) {
        refreshTokenRepository.deleteByUserEmail(user.getEmail());
    }

    @Transactional(readOnly = true)
    public void validateAuthVerifyToken(String authVerifyToken) {
        if (!authVerifyTokenRepository.existsByAuthVerifyToken(authVerifyToken)) {
            throw new AuthVerifyTokenNotFoundException();
        }
    }

    public void deleteAuthVerifyToken(String authVerifyToken) {
        authVerifyTokenRepository.findByAuthVerifyToken(authVerifyToken)
                .ifPresent(authVerifyTokenRepository::delete);
    }
}
