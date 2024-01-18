package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.response.AuthVerifyEmailResponse;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthCodeExpiredException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthEmailNotFoundException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotMatchAuthCodeException;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthEmailVerifyServiceTest {

    @InjectMocks
    private AuthEmailVerifyService authEmailVerifyService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserLoginService userLoginService;

    @Mock
    private AuthEmailRepository authEmailRepository;

    @Test
    @DisplayName("이메일 인증 정상 처리 : 기존회원")
    void authEmailSuccessExists() {
        // given
        User user = TestUtil.createUser();
        String target = user.getEmail();
        String code = "123456";
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);
        AuthEmail authEmail = AuthEmail.createAuthEmail(target, code, expiredAt);
        TokenDto tokenDto = new TokenDto("Bearer", "accessToken", "refreshToken", null, null);

        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(target))
                .thenReturn(Optional.of(authEmail));
        when(userRepository.existsByEmail(target)).thenReturn(true);
        when(userLoginService.login(target)).thenReturn(tokenDto);

        // when
        AuthVerifyEmailResponse response = authEmailVerifyService.verifyAuthEmail(code, target);

        // then
        assertThat(response.getGrantType()).isEqualTo(tokenDto.getGrantType());
        assertThat(response.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(response.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
        assertThat(response.getAuthVerifyToken()).isNull();
    }

    @Test
    @DisplayName("이메일 인증 정상 처리 : 신규회원")
    void authEmailSuccessNewUser() {
        // given
        User user = TestUtil.createUser();
        String target = user.getEmail();
        String code = "123456";
        String authVerify = "authVerifyToken";
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);
        AuthEmail authEmail = AuthEmail.createAuthEmail(target, code, expiredAt);
        AuthVerifyToken authVerifyToken = new AuthVerifyToken(authVerify, null);

        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(target))
                .thenReturn(Optional.of(authEmail));
        when(userRepository.existsByEmail(target)).thenReturn(false);
        when(tokenService.registerAuthVerifyToken()).thenReturn(authVerifyToken);

        // when
        AuthVerifyEmailResponse response = authEmailVerifyService.verifyAuthEmail(code, target);

        // then
        assertThat(response.getGrantType()).isNull();
        assertThat(response.getAccessToken()).isNull();
        assertThat(response.getRefreshToken()).isNull();
        assertThat(response.getAuthVerifyToken()).isEqualTo(authVerify);
    }

    @Test
    @DisplayName("AuthEmail entity가 없으면 AuthEmailNotFoundException 발생")
    void emptyAuthEmailEntity() {
        // given
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(any(String.class)))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> authEmailVerifyService.verifyAuthEmail("code", "target"))
                .isInstanceOf(AuthEmailNotFoundException.class);
    }

    @Test
    @DisplayName("authEmail이 유효하지 않으면 AuthEmailNotFoundException 발생")
    void authEmailInvalidate() {
        // given
        AuthEmail authEmail = AuthEmail.createAuthEmail("target", "code", LocalDateTime.now());
        authEmail.changeInvalidate();
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(any(String.class)))
                .thenReturn(Optional.of(authEmail));

        // expected
        assertThatThrownBy(() -> authEmailVerifyService.verifyAuthEmail("code", "target"))
                .isInstanceOf(AuthEmailNotFoundException.class);
    }

    @Test
    @DisplayName("authEmail이 만료되면 AuthCodeExpiredException 발생")
    void authEmailExpired() {
        // given
        AuthEmail authEmail = AuthEmail.createAuthEmail("target", "code", LocalDateTime.now().minusDays(1));
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(any(String.class)))
                .thenReturn(Optional.of(authEmail));

        // expected
        assertThatThrownBy(() -> authEmailVerifyService.verifyAuthEmail("code", "target"))
                .isInstanceOf(AuthCodeExpiredException.class);
    }

    @Test
    @DisplayName("authEmail의 code가 이 만료되면 NotMatchAuthCodeException 발생")
    void authEmailCodeNotEquals() {
        // given
        String code = "code";
        AuthEmail authEmail = AuthEmail.createAuthEmail("target", code, LocalDateTime.now().plusMinutes(1));
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(any(String.class)))
                .thenReturn(Optional.of(authEmail));

        // expected
        assertThatThrownBy(() -> authEmailVerifyService.verifyAuthEmail(code + "NotEqual", "target"))
                .isInstanceOf(NotMatchAuthCodeException.class);
    }
}