package com.playkuround.playkuroundserver.domain.auth.email.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.request.AuthEmailSendRequest;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.infra.email.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class AuthEmailSendApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthEmailRepository authEmailRepository;

    @AfterEach
    void clean() {
        authEmailRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일 인증 전송 성공")
    void sendAuthEmailSuccess() throws Exception {
        // given
        doNothing().when(emailService).sendMessage(any());

        String email = "test@konkuk.ac.kr";
        AuthEmailSendRequest attendanceRegisterRequest = new AuthEmailSendRequest(email);
        String request = objectMapper.writeValueAsString(attendanceRegisterRequest);

        // expected
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.expireAt").exists())
                .andExpect(jsonPath("$.response.sendingCount").value(1))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        String expireAt = (String) TestUtil.getJsonValue(json, "expireAt");

        List<AuthEmail> authEmails = authEmailRepository.findAll();
        assertThat(authEmails.size()).isEqualTo(1);
        assertThat(authEmails.get(0).getTarget()).isEqualTo(email);
        assertThat(authEmails.get(0).getCode()).containsPattern("[A-Za-z0-9]*");
        String entityExpiredAt = authEmails.get(0).getExpiredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertThat(entityExpiredAt).isEqualTo(expireAt);
    }

    @Test
    @DisplayName("전송 실패 : 잘못된 이메일 도메인")
    void failSendAuthEmailByWrongDomain() throws Exception {
        // given
        String[] emails = {"test@wrongDomain", "notDomain", null, "test@test@konkuk.ac.kr", "@"};

        for (String email : emails) {
            AuthEmailSendRequest attendanceRegisterRequest = new AuthEmailSendRequest(email);
            String request = objectMapper.writeValueAsString(attendanceRegisterRequest);

            // expected
            mockMvc.perform(post("/api/auth/emails")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andDo(print());
        }
    }

    @Test
    @DisplayName("전송 실패 : 초과 이메일 전송 요청")
    void failSendAuthEmailByLimitExceed() throws Exception {
        // given
        String email = "test@konkuk.ac.kr";
        for (int i = 0; i < 5; i++) {
            authEmailRepository.save(AuthEmail.createAuthEmail(email, "code", LocalDateTime.now().plusDays(1)));
        }

        AuthEmailSendRequest attendanceRegisterRequest = new AuthEmailSendRequest(email);
        String request = objectMapper.writeValueAsString(attendanceRegisterRequest);

        // expected
        mockMvc.perform(post("/api/auth/emails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.SENDING_LIMIT_EXCEEDED.getStatus().value()))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.SENDING_LIMIT_EXCEEDED.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.SENDING_LIMIT_EXCEEDED.getMessage()))
                .andDo(print());
    }

}