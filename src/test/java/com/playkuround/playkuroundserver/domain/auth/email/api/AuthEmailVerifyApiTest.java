package com.playkuround.playkuroundserver.domain.auth.email.api;

import com.jayway.jsonpath.JsonPath;
import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.token.dao.AuthVerifyTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.dao.RefreshTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.domain.GrantType;
import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
class AuthEmailVerifyApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthEmailRepository authEmailRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuthVerifyTokenRepository authVerifyTokenRepository;

    @AfterEach
    void clean() {
        authEmailRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        authVerifyTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("이메일 인증 성공 : 기존회원")
    void authEmailVerifySuccessExistsUser() throws Exception {
        // given
        User user = userRepository.findAll().get(0);
        AuthEmail authEmail = AuthEmail.createAuthEmail(user.getEmail(), "code", LocalDateTime.now().plusMinutes(5));
        authEmailRepository.save(authEmail);

        // expected
        mockMvc.perform(get("/api/auth/emails")
                        .param("email", user.getEmail())
                        .param("code", "code")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.grantType").value(GrantType.BEARER.getType()))
                .andExpect(jsonPath("$.response.accessToken").exists())
                .andExpect(jsonPath("$.response.refreshToken").exists())
                .andDo(print());

        List<AuthEmail> authEmails = authEmailRepository.findAll();
        assertThat(authEmails.size()).isEqualTo(1);
        assertThat(authEmails.get(0).isValidate()).isEqualTo(false);

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();
        assertThat(refreshTokens.size()).isEqualTo(1);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("이메일 인증 성공 : 신규회원")
    void authEmailVerifySuccessNewUser() throws Exception {
        // given
        AuthEmail authEmail = AuthEmail.createAuthEmail("user@test.com", "code", LocalDateTime.now().plusMinutes(5));
        authEmailRepository.save(authEmail);

        // expected
        MvcResult mvcResult = mockMvc.perform(get("/api/auth/emails")
                        .param("email", "user@test.com")
                        .param("code", "code")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.authVerifyToken").exists())
                .andExpect(jsonPath("$.response.grantType").doesNotExist())
                .andExpect(jsonPath("$.response.accessToken").doesNotExist())
                .andExpect(jsonPath("$.response.refreshToken").doesNotExist())
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        String authVerifyEmail = JsonPath.parse(json).read("$.response.authVerifyToken");

        List<AuthEmail> authEmails = authEmailRepository.findAll();
        assertThat(authEmails.size()).isEqualTo(1);
        assertThat(authEmails.get(0).isValidate()).isEqualTo(false);

        List<AuthVerifyToken> authVerifyTokens = authVerifyTokenRepository.findAll();
        assertThat(authVerifyTokens.size()).isEqualTo(1);
        assertThat(authVerifyTokens.get(0).getAuthVerifyToken()).isEqualTo(authVerifyEmail);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("인증 실패 : AuthEmail이 유효하지 않음")
    void authEmailInvalidate() throws Exception {
        // given
        AuthEmail authEmail = AuthEmail.createAuthEmail("test@test.com", "code", LocalDateTime.now().plusMinutes(5));
        authEmail.changeInvalidate();
        authEmailRepository.save(authEmail);

        // expected
        mockMvc.perform(get("/api/auth/emails")
                        .param("email", "test@test.com")
                        .param("code", "code")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.EMAIL_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.EMAIL_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.EMAIL_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("인증 실패 : 인증 대기 시간이 지남")
    void authEmailExpired() throws Exception {
        // given
        AuthEmail authEmail = AuthEmail.createAuthEmail("test@test.com", "code", LocalDateTime.now().minusMinutes(5));
        authEmailRepository.save(authEmail);

        // expected
        mockMvc.perform(get("/api/auth/emails")
                        .param("email", "test@test.com")
                        .param("code", "code")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.EXPIRED_AUTH_CODE.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.EXPIRED_AUTH_CODE.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.EXPIRED_AUTH_CODE.getMessage()))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("인증 실패 : 인증 코드가 일치하지 않음")
    void authEmailNotEqualsCode() throws Exception {
        // given
        AuthEmail authEmail = AuthEmail.createAuthEmail("test@test.com", "code", LocalDateTime.now().plusMinutes(5));
        authEmailRepository.save(authEmail);

        // expected
        mockMvc.perform(get("/api/auth/emails")
                        .param("email", "test@test.com")
                        .param("code", "codeNotEquals")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.NOT_MATCH_AUTH_CODE.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.NOT_MATCH_AUTH_CODE.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.NOT_MATCH_AUTH_CODE.getMessage()))
                .andDo(print());
    }
}