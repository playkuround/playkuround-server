package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenReissueService {

    private final TokenManager tokenManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenDto reissue(String refreshToken) {
        String userEmail = tokenManager.getUsernameFromToken(refreshToken);
        if (!refreshTokenRepository.existsByUserEmail(userEmail)) {
            throw new InvalidRefreshTokenException();
        }

        TokenDto tokenDto = tokenManager.createTokenDto(userEmail);
        tokenService.saveRefreshToken(userEmail, tokenDto.getRefreshToken());

        return tokenDto;
    }
}
