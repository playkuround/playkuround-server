package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;

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

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

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
        AuthenticationManager authenticationManager
                = authentication -> new UsernamePasswordAuthenticationToken(email, null);
        when(tokenManager.createTokenDto(any(Authentication.class))).thenReturn(tokenDto);
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        doNothing().when(tokenService).registerRefreshToken(any(Authentication.class), any(String.class));

        // when
        TokenDto response = userLoginService.login(email);

        // then
        assertThat(response.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(response.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
        assertThat(response.getGrantType()).isEqualTo(tokenDto.getGrantType());
    }

}