package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.domain.user.exception.UserEmailDuplicationException;
import com.playkuround.playkuroundserver.domain.user.exception.UserNicknameDuplicationException;
import com.playkuround.playkuroundserver.domain.user.exception.UserNicknameUnavailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegisterServiceTest {

    @InjectMocks
    private UserRegisterService userRegisterService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserLoginService userLoginService;

    private final String nickname = "tester";
    private final String major = "컴퓨터공학부";
    private final String email = "tester@konkuk.ac.kr";

    @Nested
    @DisplayName("회원가입")
    class signup {

        @Test
        @DisplayName("정상적으로 성공")
        void success_1() {
            // given
            TokenDto tokenDto = TokenDto.builder()
                    .grantType("Bearer")
                    .accessToken("accessToken")
                    .refreshToken("refreshToken")
                    .accessTokenExpiredAt(new Date())
                    .refreshTokenExpiredAt(new Date())
                    .build();
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
            when(userLoginService.login(email))
                    .thenReturn(tokenDto);

            // when
            UserRegisterDto userRegisterDto = new UserRegisterDto(email, nickname, Major.valueOf(major));
            TokenDto result = userRegisterService.registerUser(userRegisterDto);

            // then
            assertThat(result.getGrantType()).isEqualTo(tokenDto.getGrantType());
            assertThat(result.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
            assertThat(result.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
        }

        @Test
        @DisplayName("이미 존재하는 이메일이면 에러가 발생한다.")
        void fail_1() {
            // given
            when(userRepository.existsByEmail(email)).thenReturn(true);

            // expect
            UserRegisterDto userRegisterDto = new UserRegisterDto(email, nickname, Major.valueOf(major));
            assertThatThrownBy(() -> userRegisterService.registerUser(userRegisterDto))
                    .isInstanceOf(UserEmailDuplicationException.class);
        }

        @Test
        @DisplayName("이미 존재하는 닉네임이면 에러가 발생한다.")
        void fail_2() {
            // given
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(userRepository.existsByNickname(nickname)).thenReturn(true);

            // expect
            UserRegisterDto userRegisterDto = new UserRegisterDto(email, nickname, Major.valueOf(major));
            assertThatThrownBy(() -> userRegisterService.registerUser(userRegisterDto))
                    .isInstanceOf(UserNicknameDuplicationException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"존나", "개새끼", "씨발", "시발"})
        @DisplayName("BadWordCheck에 걸리는 닉네임은 에러가 발생한다.")
        void fail_3(String badWord) {
            // given
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(userRepository.existsByNickname(badWord)).thenReturn(false);

            // expect
            UserRegisterDto userRegisterDto = new UserRegisterDto(email, badWord, Major.valueOf(major));
            assertThatThrownBy(() -> userRegisterService.registerUser(userRegisterDto))
                    .isInstanceOf(UserNicknameUnavailableException.class);
        }
    }

}