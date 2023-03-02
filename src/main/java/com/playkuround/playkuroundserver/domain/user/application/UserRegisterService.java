package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRegisterService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final TokenManager tokenManager;
    private final TokenService tokenService;

    public UserRegisterDto.Response registerUser(UserRegisterDto.Request registerRequest) {
        // 중복 검사
        userValidator.validateDuplicateEmail(registerRequest.getEmail());
        userValidator.validateDuplicateNickName(registerRequest.getNickname());

        // DTO를 엔티티로 변환 후 DB에 저장
        User user = userRepository.save(registerRequest.toEntity());

        // 응답으로 반환할 토큰 생성
        // 리프레시 토큰 레디스에 저장
        TokenDto tokenDto = tokenManager.createTokenDto(user.getEmail());
        tokenService.registerRefreshToken(user.getEmail(), tokenDto.getRefreshToken());

        return UserRegisterDto.Response.of(tokenDto);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

}
