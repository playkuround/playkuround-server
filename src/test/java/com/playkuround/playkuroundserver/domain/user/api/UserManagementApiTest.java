package com.playkuround.playkuroundserver.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.user.api.request.UserRegisterRequest;
import com.playkuround.playkuroundserver.domain.user.api.response.UserRegisterResponse;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationControllerTest
class UserManagementApiTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenManager tokenManager;

    private final String nickname = "tester";
    private final Major major = Major.컴퓨터공학부;
    private final String email = "tester@konkuk.ac.kr";

    @AfterEach
    void afterEach() {
        userRepository.deleteAllInBatch();
        refreshTokenRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원 등록 성공")
    void userRegisterSuccess() throws Exception {
        // given
        AuthVerifyToken authVerifyToken = tokenService.saveAuthVerifyToken();

        // when
        UserRegisterRequest registerRequest
                = new UserRegisterRequest(email, nickname, major.name(), authVerifyToken.getAuthVerifyToken());
        String request = objectMapper.writeValueAsString(registerRequest);

        MvcResult mvcResult = mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.grantType").value(GrantType.BEARER.getType()))
                .andExpect(jsonPath("$.response.accessToken").exists())
                .andExpect(jsonPath("$.response.refreshToken").exists())
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        UserRegisterResponse response = TestUtil.convertFromJsonStringToObject(json, UserRegisterResponse.class);

        // then
        assertThat(tokenManager.isValidateToken(response.getAccessToken())).isTrue();

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserEmail(email);
        assertThat(optionalRefreshToken).isPresent();
        RefreshToken refreshToken = optionalRefreshToken.get();
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken.getRefreshToken());

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);

        User user = users.get(0);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getMajor()).isEqualTo(major);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(user.getAttendanceDays()).isZero();
    }

    @Test
    @DisplayName("회원 등록 실패 - 존재하지 않는 AuthVerifyToken")
    void userRegisterFailByNotExitsAuthVerifyToken() throws Exception {
        // when
        UserRegisterRequest registerRequest
                = new UserRegisterRequest(email, nickname, major.name(), "NotFoundToken");
        String request = objectMapper.writeValueAsString(registerRequest);

        // expect
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_TOKEN.getStatus().value()))
                .andDo(print());
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("회원 등록 실패 - 중복 이메일")
    void userRegisterFailByDuplicateEmail() throws Exception {
        // given
        User user = User.create(email, nickname, major, Role.ROLE_USER);
        userRepository.save(user);
        AuthVerifyToken authVerifyToken = tokenService.saveAuthVerifyToken();

        // when
        UserRegisterRequest registerRequest
                = new UserRegisterRequest(email, nickname + "0", major.name(), authVerifyToken.getAuthVerifyToken());
        String request = objectMapper.writeValueAsString(registerRequest);

        // expect
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.EMAIL_DUPLICATION.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.EMAIL_DUPLICATION.getMessage()))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.EMAIL_DUPLICATION.getStatus().value()))
                .andDo(print());
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
    }

    @Test
    @DisplayName("회원 등록 실패 - 중복 닉네임")
    void userRegisterFailByDuplicateNickname() throws Exception {
        // given
        User user = User.create(email, nickname, major, Role.ROLE_USER);
        userRepository.save(user);
        AuthVerifyToken authVerifyToken = tokenService.saveAuthVerifyToken();

        // when
        UserRegisterRequest registerRequest
                = new UserRegisterRequest("k" + email, nickname, major.name(), authVerifyToken.getAuthVerifyToken());
        String request = objectMapper.writeValueAsString(registerRequest);

        // expect
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.NICKNAME_DUPLICATION.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.NICKNAME_DUPLICATION.getMessage()))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.NICKNAME_DUPLICATION.getStatus().value()))
                .andDo(print());
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("로그아웃 - 리프레시 토큰 삭제")
    void logout() throws Exception {
        // given
        RefreshToken refreshToken = tokenManager.createRefreshTokenEntity(email, "refreshToken");
        refreshTokenRepository.save(refreshToken);

        // when
        mockMvc.perform(post("/api/users/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print());

        // then
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserEmail(email);
        assertThat(optionalRefreshToken).isEmpty();
    }
}