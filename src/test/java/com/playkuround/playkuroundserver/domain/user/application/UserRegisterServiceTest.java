package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.domain.user.exception.UserEmailDuplicationException;
import com.playkuround.playkuroundserver.domain.user.exception.UserNicknameDuplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegisterServiceTest {
    @InjectMocks
    private UserRegisterService userRegisterService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private TokenService tokenService;

    private final String email = "tester@konkuk.ac.kr";
    private final String nickname = "tester";
    private final String major = "컴퓨터공학부";
    private final TokenDto tokenDto = TokenDto.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .grantType("Bearer")
            .accessTokenExpiredAt(new Date())
            .refreshTokenExpiredAt(new Date())
            .build();

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {
        // given
        when(userRepository.existsByEmail(email))
                .thenReturn(false);
        when(userRepository.existsByNickname(nickname))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .then(invocation -> {
                    User savedUser = invocation.getArgument(0);
                    ReflectionTestUtils.setField(savedUser, "id", 1L);
                    return savedUser;
                });
        when(tokenManager.createTokenDto(email))
                .thenReturn(tokenDto);
        doNothing().when(tokenService).registerRefreshToken(any(User.class), any(String.class));

        // when
        UserRegisterDto.Request registerRequest = new UserRegisterDto.Request(email, nickname, major);
        UserRegisterDto.Response result = userRegisterService.registerUser(registerRequest);

        // then
        assertThat(result.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(result.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
        assertThat(result.getGrantType()).isEqualTo(tokenDto.getGrantType());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void signupFailByExistsEmail() {
        // given
        when(userRepository.existsByEmail(email))
                .thenReturn(true);

        // when
        UserRegisterDto.Request registerRequest = new UserRegisterDto.Request(email, nickname, major);
        assertThrows(UserEmailDuplicationException.class,
                () -> userRegisterService.registerUser(registerRequest));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 닉네임")
    void signupFailByExistsNickname() {
        // given
        when(userRepository.existsByEmail(email))
                .thenReturn(false);
        when(userRepository.existsByNickname(nickname))
                .thenReturn(true);

        // when
        UserRegisterDto.Request registerRequest = new UserRegisterDto.Request(email, nickname, major);
        assertThrows(UserNicknameDuplicationException.class,
                () -> userRegisterService.registerUser(registerRequest));
    }
}