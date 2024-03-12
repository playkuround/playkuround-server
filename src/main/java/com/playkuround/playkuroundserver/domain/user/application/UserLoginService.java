package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final TokenManager tokenManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public TokenDto login(String userEmail) {
        boolean isExistsUser = userRepository.existsByEmail(userEmail);
        if (!isExistsUser) {
            throw new UserNotFoundException();
        }
        TokenDto tokenDto = tokenManager.createTokenDto(userEmail);
        tokenService.registerRefreshToken(userEmail, tokenDto.getRefreshToken());

        return tokenDto;
    }

}
