package com.playkuround.playkuroundserver.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.request.UserRegisterRequest;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserRegisterResponse;
import com.playkuround.playkuroundserver.domain.user.exception.UserEmailDuplicationException;
import com.playkuround.playkuroundserver.domain.user.exception.UserNicknameDuplicationException;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserRegisterServiceTest {
    @InjectMocks
    private UserRegisterService userRegisterService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserLoginService userLoginService;

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
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByNickname(nickname)).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .then(invocation -> {
                    User savedUser = invocation.getArgument(0);
                    ReflectionTestUtils.setField(savedUser, "id", 1L);
                    return savedUser;
                });
        when(userLoginService.login(email)).thenReturn(tokenDto);

        // when
        UserRegisterRequest registerRequest
                = new UserRegisterRequest(email, nickname, major, "");
        UserRegisterResponse result = userRegisterService.registerUser(registerRequest);

        // then
        assertThat(result.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(result.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
        assertThat(result.getGrantType()).isEqualTo(tokenDto.getGrantType());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void signupFailByExistsEmail() {
        // given
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // expect
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, nickname, major, "");
        assertThrows(UserEmailDuplicationException.class,
                () -> userRegisterService.registerUser(registerRequest));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 닉네임")
    void signupFailByExistsNickname() {
        // given
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByNickname(nickname)).thenReturn(true);

        // expect
        UserRegisterRequest registerRequest = new UserRegisterRequest(email, nickname, major, "");
        assertThrows(UserNicknameDuplicationException.class,
                () -> userRegisterService.registerUser(registerRequest));
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteUser() {
        // given
        User user = new User(email, nickname, Major.컴퓨터공학부, Role.ROLE_USER);
        doNothing().when(userRepository).delete(user);

        // when
        userRegisterService.deleteUser(user);

        // then
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).delete(argument.capture());
        assertThat(argument.getValue()).isEqualTo(user);
    }
}