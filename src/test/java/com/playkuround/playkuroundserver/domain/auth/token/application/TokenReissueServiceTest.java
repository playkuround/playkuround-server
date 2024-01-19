package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.dto.response.TokenReissueResponse;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidRefreshTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenReissueServiceTest {

    @InjectMocks
    private TokenReissueService tokenReissueService;

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissueSuccess() {
        // given
        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated("username", null, null);
        when(tokenManager.authentication("accessToken")).thenReturn(authenticated);
        when(tokenManager.isValidateToken("refreshToken")).thenReturn(true);
        when(refreshTokenRepository.existsByUserEmail("username")).thenReturn(true);

        TokenDto tokenDto = new TokenDto("newGrantType", "newAccessToken", "newRefreshToken", null, null);
        when(tokenManager.createTokenDto("username")).thenReturn(tokenDto);

        doNothing().when(tokenService).registerRefreshToken("username", "newRefreshToken");

        // when
        TokenReissueResponse response = tokenReissueService.reissue("accessToken", "refreshToken");

        // then
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
        assertThat(response.getGrantType()).isEqualTo("newGrantType");
    }

    @Test
    @DisplayName("토큰 재발급 실패 : 유효하지 않은 refreshToken")
    void reissueFailByInvalidateToken() {
        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated("username", null, null);
        when(tokenManager.authentication("accessToken")).thenReturn(authenticated);
        when(tokenManager.isValidateToken("refreshToken")).thenReturn(false);

        // when
        assertThatThrownBy(() -> tokenReissueService.reissue("accessToken", "refreshToken"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    @DisplayName("토큰 재발급 실패 : refreshToken이 저장소에 존재하지 않음")
    void reissueFailByNotFoundRefreshToken() {
        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated("username", null, null);
        when(tokenManager.authentication("accessToken")).thenReturn(authenticated);
        when(tokenManager.isValidateToken("refreshToken")).thenReturn(true);
        when(refreshTokenRepository.existsByUserEmail("username")).thenReturn(false);

        // when
        assertThatThrownBy(() -> tokenReissueService.reissue("accessToken", "refreshToken"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }
}