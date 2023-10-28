package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.AuthVerifyTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.auth.token.exception.AuthVerifyTokenNotFoundException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {
    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthVerifyTokenRepository authVerifyTokenRepository;

    public void registerRefreshToken(Authentication authentication, String sRefreshToken) {
        refreshTokenRepository.deleteAllByUserEmail(authentication.getName());

        RefreshToken refreshToken = tokenManager.createRefreshToken(authentication, sRefreshToken);
        refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshTokenByUser(User user) {
        refreshTokenRepository.deleteAllByUserEmail(user.getEmail());
    }

    public AuthVerifyToken registerAuthVerifyToken() {
        AuthVerifyToken authVerifyToken = tokenManager.createAuthVerifyToken();
        return authVerifyTokenRepository.save(authVerifyToken);
    }

    @Transactional(readOnly = true)
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
