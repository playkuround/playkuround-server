package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserLoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLoginService {

    private final TokenManager tokenManager;
    private final TokenService tokenService;

    public UserLoginDto.Response login(User user) {
        TokenDto tokenDto = tokenManager.createTokenDto(user.getEmail());
        tokenService.registerRefreshToken(user, tokenDto.getRefreshToken());

        return UserLoginDto.Response.from(tokenDto);
    }

}
