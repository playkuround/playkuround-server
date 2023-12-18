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

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {
    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthVerifyTokenRepository authVerifyTokenRepository;

    public void registerRefreshToken(Authentication authentication, String sRefreshToken) {
        refreshTokenRepository.deleteByUserEmail(authentication.getName());

        RefreshToken refreshToken = tokenManager.createRefreshToken(authentication, sRefreshToken);
        refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshTokenByUser(User user) {
        refreshTokenRepository.deleteByUserEmail(user.getEmail());
    }

    public AuthVerifyToken registerAuthVerifyToken() {
        AuthVerifyToken authVerifyToken = tokenManager.createAuthVerifyToken();
        return authVerifyTokenRepository.save(authVerifyToken);
    }

    @Transactional(readOnly = true)
    public void validateAuthVerifyToken(String authVerifyToken) {
        // TODO: 테스트용 코드, 추후 삭제
        if (Objects.equals(authVerifyToken, "testToken")) {
            return;
        }
        if (!authVerifyTokenRepository.existsByAuthVerifyToken(authVerifyToken)) {
            throw new AuthVerifyTokenNotFoundException();
        }
    }

    public void deleteAuthVerifyToken(String authVerifyToken) {
        authVerifyTokenRepository.findByAuthVerifyToken(authVerifyToken)
                .ifPresent(authVerifyTokenRepository::delete);
    }
}
