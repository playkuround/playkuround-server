package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.dto.response.TokenReissueResponse;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenReissueService {

    private final TokenManager tokenManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenReissueResponse reissue(String accessToken, String refreshToken) {
        Authentication authentication = tokenManager.authentication(accessToken);
        String username = authentication.getName();

        validateRefreshToken(username, refreshToken);

        TokenDto tokenInfo = tokenManager.createTokenDto(username);
        tokenService.registerRefreshToken(username, tokenInfo.getRefreshToken());

        return TokenReissueResponse.from(tokenInfo);
    }

    private void validateRefreshToken(String userEmail, String refreshToken) {
        if (!tokenManager.isValidateToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        if (!refreshTokenRepository.existsByUserEmail(userEmail)) {
            throw new InvalidRefreshTokenException();
        }
    }
}
