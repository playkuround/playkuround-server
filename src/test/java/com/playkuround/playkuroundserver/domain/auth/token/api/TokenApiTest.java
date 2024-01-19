package com.playkuround.playkuroundserver.domain.auth.token.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.auth.token.dto.request.TokenReissueRequest;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
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
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissueSuccess() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);
        TokenDto tokenDto = userLoginService.login(user.getEmail());

        TokenReissueRequest tokenReissueRequest = new TokenReissueRequest(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
        String request = objectMapper.writeValueAsString(tokenReissueRequest);

        // expected
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.grantType").value(GrantType.BEARER.getType()))
                .andExpect(jsonPath("$.response.accessToken").exists())
                .andExpect(jsonPath("$.response.refreshToken").exists())
                .andDo(print());

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("토큰 재발급 실패 : 유효하지 않은 refreshToken")
    void reissueFailByInvalidateRefreshToken() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);
        TokenDto tokenDto = userLoginService.login(user.getEmail());

        TokenReissueRequest tokenReissueRequest = new TokenReissueRequest(tokenDto.getAccessToken(), "invalidateRefreshToken");
        String request = objectMapper.writeValueAsString(tokenReissueRequest);

        // expected
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_TOKEN.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("토큰 재발급 실패 : 존재하지 않는 refreshToken")
    void reissueFailByNotFoundRefreshToken() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);
        TokenDto tokenDto = userLoginService.login(user.getEmail());
        refreshTokenRepository.deleteAll();

        TokenReissueRequest tokenReissueRequest = new TokenReissueRequest(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
        String request = objectMapper.writeValueAsString(tokenReissueRequest);

        // expected
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_TOKEN.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("토큰 재발급 실패 : accessToken이 유효하지 않음")
    void reissueFailByInvalidAccessToken() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);
        TokenDto tokenDto = userLoginService.login(user.getEmail());

        TokenReissueRequest tokenReissueRequest = new TokenReissueRequest("invalidAccessToken", tokenDto.getRefreshToken());
        String request = objectMapper.writeValueAsString(tokenReissueRequest);

        // expected
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_TOKEN.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("토큰 재발급 실패 : 존재하지 않는 유저")
    void reissueFailByNotFoundUser() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);
        TokenDto tokenDto = userLoginService.login(user.getEmail());
        userRepository.deleteAll();

        TokenReissueRequest tokenReissueRequest = new TokenReissueRequest(tokenDto.getAccessToken(), tokenDto.getRefreshToken());
        String request = objectMapper.writeValueAsString(tokenReissueRequest);

        // expected
        mockMvc.perform(post("/api/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.USER_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andDo(print());
    }
}