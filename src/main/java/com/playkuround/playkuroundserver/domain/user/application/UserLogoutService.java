package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLogoutService {

    private final TokenService tokenService;

    public void logout(User user) {
        tokenService.deleteRefreshTokenByUser(user);
    }

}
