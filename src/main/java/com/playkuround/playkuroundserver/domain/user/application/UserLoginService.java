package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserLoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLoginService {

    private final UserFindDao userFindDao;
    private final UserValidator userValidator;
    private final TokenManager tokenManager;

    public UserLoginDto.Response login(String userEmail) {
        // 가입된 유저인지 확인
        userValidator.validateRegisteredUser(userEmail);

        // 응답으로 반환할 토큰 생성
        // 유저 리프레시 토큰 갱신
        TokenDto tokenDto = tokenManager.createTokenDto(userEmail);
        User user = userFindDao.findByEmail(userEmail);
        user.updateRefreshToken(tokenDto);

        return UserLoginDto.Response.of(tokenDto);
    }

}
