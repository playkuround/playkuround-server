package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dto.UserLoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLoginService {

    private final UserValidator userValidator;
    private final TokenManager tokenManager;
    private final TokenService tokenService;

    public UserLoginDto.Response login(String userEmail) {
        // 가입된 유저인지 확인
        userValidator.validateRegisteredUser(userEmail);

        // 응답으로 반환할 토큰 생성
        // 리프레시 토큰 레디스에 저장
        TokenDto tokenDto = tokenManager.createTokenDto(userEmail);
        tokenService.registerRefreshToken(userEmail, tokenDto.getRefreshToken());

        return UserLoginDto.Response.of(tokenDto);
    }

}
