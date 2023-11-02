package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenReissueRequest;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenReissueResponse;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenReissueService {

    private final TokenManager tokenManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenReissueResponse reissue(TokenReissueRequest request) {
        Authentication authentication = tokenManager.getAuthentication(request.getAccessToken());
        validateRefreshToken(request.getRefreshToken(), authentication);

        TokenDto tokenInfo = tokenManager.createTokenDto(authentication);
        tokenService.registerRefreshToken(authentication, request.getRefreshToken());

        return TokenReissueResponse.from(tokenInfo);
    }

    private void validateRefreshToken(String sRefreshToken, Authentication authentication) {
        if (!tokenManager.isValidateToken(sRefreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        if (!refreshTokenRepository.existsById(authentication.getName())) {
            throw new InvalidRefreshTokenException();
        }
    }
}
