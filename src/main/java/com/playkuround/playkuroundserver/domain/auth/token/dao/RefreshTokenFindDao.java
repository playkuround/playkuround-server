package com.playkuround.playkuroundserver.domain.auth.token.dao;

import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.auth.token.exception.RefreshTokenNotFoundException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenFindDao {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByUser(User user) {
        return refreshTokenRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new RefreshTokenNotFoundException(user.getEmail()));
    }

}
