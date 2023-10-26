package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserLoginDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

    @InjectMocks
    private UserLoginService userLoginService;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("로그인 성공")
    void signupSuccess() {
        // given
        String email = "tester@konkuk.ac.kr";
        TokenDto tokenDto = TokenDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .grantType("Bearer")
                .accessTokenExpiredAt(new Date())
                .refreshTokenExpiredAt(new Date())
                .build();
        when(tokenManager.createTokenDto(email))
                .thenReturn(tokenDto);
        doNothing().when(tokenService).registerRefreshToken(any(User.class), any(String.class));

        // when
        UserLoginDto.Response result = userLoginService.login(new User(email, "nickname", Major.컴퓨터공학부));

        // then
        assertThat(result.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(result.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
        assertThat(result.getGrantType()).isEqualTo(tokenDto.getGrantType());
    }

}