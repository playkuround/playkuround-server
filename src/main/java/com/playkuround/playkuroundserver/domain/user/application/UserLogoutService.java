package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLogoutService {

    private final UserFindDao userFindDao;
    private final TokenService tokenService;

    public void logout(String userEmail) {
        tokenService.deleteRefreshTokenByUserEmail(userEmail);
    }

}
