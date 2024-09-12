package com.playkuround.playkuroundserver.domain.auth.token.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.auth.token.api.request.TokenReissueRequest;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationControllerTest
class TokenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginService userLoginService;

    @AfterEach
    void clean() {
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("refreshToken이 유효한 상태라면, accessToken과 refreshToken이 모두 재발급된다.")
    void reissueSuccess() throws Exception {
        // given
        User user = userRepository.save(TestUtil.createUser());
        TokenDto tokenDto = userLoginService.login(user.getEmail());

        TokenReissueRequest tokenReissueRequest = new TokenReissueRequest(tokenDto.getRefreshToken());
        String request = objectMapper.writeValueAsString(tokenReissueRequest);

        // expected
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.grantType").value(GrantType.BEARER.getType()))
                .andExpect(jsonPath("$.response.accessToken").exists())
                .andExpect(jsonPath("$.response.refreshToken").exists())
                .andDo(print());

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens).hasSize(1)
                .extracting("userEmail")
                .containsExactly(user.getEmail());
    }

    @Test
    @DisplayName("토큰 재발급 실패 : 유효하지 않은 refreshToken")
    void reissueFailByInvalidateRefreshToken() throws Exception {
        // given
        TokenReissueRequest tokenReissueRequest = new TokenReissueRequest("invalidateRefreshToken");
        String request = objectMapper.writeValueAsString(tokenReissueRequest);

        // expected
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_TOKEN.getStatus().value()))
                .andDo(print());

        assertThat(refreshTokenRepository.count()).isZero();
    }

    @Test
    @DisplayName("토큰 재발급 실패 : 존재하지 않는 refreshToken")
    void reissueFailByNotFoundRefreshToken() throws Exception {
        // given
        User user = userRepository.save(TestUtil.createUser());
        TokenDto tokenDto = userLoginService.login(user.getEmail());
        refreshTokenRepository.deleteAll();

        TokenReissueRequest tokenReissueRequest = new TokenReissueRequest(tokenDto.getRefreshToken());
        String request = objectMapper.writeValueAsString(tokenReissueRequest);

        // expected
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_TOKEN.getStatus().value()))
                .andDo(print());

        assertThat(refreshTokenRepository.count()).isZero();
    }
}