package com.playkuround.playkuroundserver.domain.token.application;

import com.playkuround.playkuroundserver.domain.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.token.exception.RefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRedisRepository;

    public void registerRefreshToken(RefreshToken refreshToken){
        refreshTokenRedisRepository.save(refreshToken);
    }

    public RefreshToken getRefreshTokenByEmail(String email){
        RefreshToken refreshToken = refreshTokenRedisRepository.findById(email)
                .orElseThrow(()-> new RefreshTokenNotFoundException(email));
        return refreshToken;
    }

    public void removeRefreshToken(String email) {
        refreshTokenRedisRepository.deleteById(email);
    }

}

