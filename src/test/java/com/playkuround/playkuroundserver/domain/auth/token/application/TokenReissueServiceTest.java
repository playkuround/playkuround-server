package com.playkuround.playkuroundserver.domain.auth.token.application;

import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.dto.response.TokenReissueResponse;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidRefreshTokenException;
import com.playkuround.playkuroundserver.domain.auth.token.exception.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        String refreshToken = "refreshToken";
        String username = "username";
        when(tokenManager.getUsernameFromToken(refreshToken)).thenReturn(username);
        when(refreshTokenRepository.existsByUserEmail(username)).thenReturn(true);

        TokenDto tokenDto =
                new TokenDto("newGrantType", "newAccessToken", "newRefreshToken",
                        null, null);
        when(tokenManager.createTokenDto(username)).thenReturn(tokenDto);

        // when
        TokenReissueResponse response = tokenReissueService.reissue(refreshToken);

        // then
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
        assertThat(response.getGrantType()).isEqualTo("newGrantType");
    }

    @Test
    @DisplayName("토큰 재발급 실패 : 유효하지 않은 refreshToken")
    void reissueFailByInvalidateToken() {
        // given
        String refreshToken = "refreshToken";
        when(tokenManager.getUsernameFromToken(refreshToken))
                .thenThrow(InvalidTokenException.class);

        // when
        assertThatThrownBy(() -> tokenReissueService.reissue(refreshToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("토큰 재발급 실패 : refreshToken이 저장소에 존재하지 않음")
    void reissueFailByNotFoundRefreshToken() {
        String refreshToken = "refreshToken";
        String username = "username";
        when(tokenManager.getUsernameFromToken(refreshToken)).thenReturn(username);
        when(refreshTokenRepository.existsByUserEmail(username)).thenReturn(false);

        // when
        assertThatThrownBy(() -> tokenReissueService.reissue(refreshToken))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }
}