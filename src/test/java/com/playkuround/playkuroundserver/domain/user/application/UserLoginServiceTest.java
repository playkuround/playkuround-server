package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private UserRepository userRepository;

    @Test
    @DisplayName("로그인 성공")
    void signupSuccess() {
        // given
        String email = "tester@konkuk.ac.kr";
        TokenDto tokenDto = TokenDto.builder()
                .grantType("Bearer")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .accessTokenExpiredAt(new Date())
                .refreshTokenExpiredAt(new Date())
                .build();
        when(tokenManager.createTokenDto(any(String.class)))
                .thenReturn(tokenDto);
        when(userRepository.existsByEmail(email))
                .thenReturn(true);

        // when
        TokenDto response = userLoginService.login(email);

        // then
        assertThat(response.getGrantType()).isEqualTo(tokenDto.getGrantType());
        assertThat(response.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(response.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
    }

}