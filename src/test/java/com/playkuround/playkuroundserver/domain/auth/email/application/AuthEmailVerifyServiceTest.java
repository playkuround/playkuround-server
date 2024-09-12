package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthVerifyEmailResult;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthVerifyTokenResult;
import com.playkuround.playkuroundserver.domain.auth.email.dto.TokenDtoResult;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthCodeExpiredException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthEmailNotFoundException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotMatchAuthCodeException;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.exception.UserNotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthEmailVerifyServiceTest {

    @InjectMocks
    private AuthEmailVerifyServiceImpl authEmailVerifyService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserLoginService userLoginService;

    @Mock
    private AuthEmailRepository authEmailRepository;

    @Mock
    private DateTimeService dateTimeService;

    @Test
    @DisplayName("이메일 인증 정상 처리 : 기존회원인 경우 TokenDto 반환")
    void authEmailSuccessExists() {
        // given
        User user = TestUtil.createUser();
        String target = user.getEmail();
        String code = "123456";
        LocalDateTime expiredAt = LocalDateTime.of(2024, 7, 1, 0, 0);
        AuthEmail authEmail = AuthEmail.createAuthEmail(target, code, expiredAt);
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(target))
                .thenReturn(Optional.of(authEmail));
        when(dateTimeService.getLocalDateTimeNow())
                .thenReturn(expiredAt.minusHours(1));

        TokenDto tokenDto = new TokenDto("Bearer", "accessToken", "refreshToken", null, null);
        when(userLoginService.login(target)).thenReturn(tokenDto);

        // when
        AuthVerifyEmailResult result = authEmailVerifyService.verifyAuthEmail(code, target);

        // then
        assertThat(result).isInstanceOf(TokenDtoResult.class);
        TokenDtoResult tokenDtoResult = (TokenDtoResult) result;
        assertThat(tokenDtoResult.grantType()).isEqualTo(tokenDto.getGrantType());
        assertThat(tokenDtoResult.accessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(tokenDtoResult.refreshToken()).isEqualTo(tokenDto.getRefreshToken());
    }

    @Test
    @DisplayName("이메일 인증 정상 처리 : 신규회원인 경우 authVerifyToken 반환")
    void authEmailSuccessNewUser() {
        // given
        User user = TestUtil.createUser();
        String target = user.getEmail();
        String code = "123456";
        String authVerify = "authVerifyToken";
        LocalDateTime expiredAt = LocalDateTime.of(2024, 7, 1, 0, 0);
        AuthEmail authEmail = AuthEmail.createAuthEmail(target, code, expiredAt);
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(target))
                .thenReturn(Optional.of(authEmail));
        when(dateTimeService.getLocalDateTimeNow())
                .thenReturn(expiredAt.minusHours(1));
        when(userLoginService.login(target))
                .thenThrow(UserNotFoundException.class);

        AuthVerifyToken authVerifyToken = new AuthVerifyToken(authVerify, null);
        when(tokenService.saveAuthVerifyToken()).thenReturn(authVerifyToken);

        // when
        AuthVerifyEmailResult result = authEmailVerifyService.verifyAuthEmail(code, target);

        // then
        assertThat(result).isInstanceOf(AuthVerifyTokenResult.class);
        AuthVerifyTokenResult authVerifyTokenResult = (AuthVerifyTokenResult) result;
        assertThat(authVerifyTokenResult.authVerifyToken()).isEqualTo(authVerify);
    }

    @Test
    @DisplayName("AuthEmail entity가 없으면 AuthEmailNotFoundException 발생한다.")
    void emptyAuthEmailEntity() {
        // given
        String target = "test@konkuk.ac.kr";
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(target))
                .thenReturn(Optional.empty());

        // expected
        assertThatThrownBy(() -> authEmailVerifyService.verifyAuthEmail("code", target))
                .isInstanceOf(AuthEmailNotFoundException.class);
    }

    @Test
    @DisplayName("authEmail이 유효하지 않으면 AuthEmailNotFoundException 발생한다.")
    void authEmailInvalidate() {
        // given
        AuthEmail authEmail = AuthEmail.createAuthEmail("test@konkuk.ac.kr", "code", LocalDateTime.now());
        authEmail.changeInvalidate();
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(authEmail.getTarget()))
                .thenReturn(Optional.of(authEmail));

        // expected
        assertThatThrownBy(() -> authEmailVerifyService.verifyAuthEmail(authEmail.getCode(), authEmail.getTarget()))
                .isInstanceOf(AuthEmailNotFoundException.class);
    }

    @Test
    @DisplayName("authEmail이 만료되면 AuthCodeExpiredException 발생한다.")
    void authEmailExpired() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 7, 1, 0, 0);
        when(dateTimeService.getLocalDateTimeNow())
                .thenReturn(now);

        AuthEmail authEmail = AuthEmail.createAuthEmail("test@konkuk.ac.kr", "code", now.minusDays(1));
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(authEmail.getTarget()))
                .thenReturn(Optional.of(authEmail));

        // expected
        assertThatThrownBy(() -> authEmailVerifyService.verifyAuthEmail(authEmail.getCode(), authEmail.getTarget()))
                .isInstanceOf(AuthCodeExpiredException.class);
    }

    @Test
    @DisplayName("authEmail의 code가 일치하지 않으면 NotMatchAuthCodeException 발생한다.")
    void authEmailCodeNotEquals() {
        // given
        LocalDateTime now = LocalDateTime.now();
        when(dateTimeService.getLocalDateTimeNow())
                .thenReturn(now);

        AuthEmail authEmail = AuthEmail.createAuthEmail("test@konkuk.ac.kr", "code", now.plusMinutes(1));
        when(authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(authEmail.getTarget()))
                .thenReturn(Optional.of(authEmail));

        // expected
        assertThatThrownBy(() -> authEmailVerifyService.verifyAuthEmail(authEmail.getCode() + "NotEqual", authEmail.getTarget()))
                .isInstanceOf(NotMatchAuthCodeException.class);
    }
}