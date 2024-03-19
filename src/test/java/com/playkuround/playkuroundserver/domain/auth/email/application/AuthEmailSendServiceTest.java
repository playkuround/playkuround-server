package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthEmailInfo;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotKUEmailException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.SendingLimitExceededException;
import com.playkuround.playkuroundserver.infra.email.EmailService;
import com.playkuround.playkuroundserver.infra.email.Mail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthEmailSendServiceTest {

    @InjectMocks
    private AuthEmailSendService authEmailSendService;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthEmailRepository authEmailRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authEmailSendService, "codeLength", 6L);
        ReflectionTestUtils.setField(authEmailSendService, "maxSendingCount", 3L);
        ReflectionTestUtils.setField(authEmailSendService, "emailDomain", "test.com");
        ReflectionTestUtils.setField(authEmailSendService, "codeExpirationSeconds", 300L);
    }

    @Test
    @DisplayName("이메일 정상 정송")
    void sendAuthEmail_1() {
        // given
        when(authEmailRepository.countByTargetAndCreatedAtAfter(any(String.class), any(LocalDateTime.class)))
                .thenReturn(0L);

        // when
        String target = "test@test.com";
        AuthEmailInfo result = authEmailSendService.sendAuthEmail(target);

        // then
        assertThat(result.sendingCount()).isEqualTo(1L);

        ArgumentCaptor<AuthEmail> authEmailArgument = ArgumentCaptor.forClass(AuthEmail.class);
        verify(authEmailRepository, times(1)).save(authEmailArgument.capture());
        AuthEmail authEmail = authEmailArgument.getValue();
        assertThat(authEmail.getTarget()).isEqualTo(target);
        assertThat(authEmail.getCode()).containsPattern("[0-9a-zA-Z]{6}");

        ArgumentCaptor<Mail> mailArgument = ArgumentCaptor.forClass(Mail.class);
        verify(emailService, times(1)).sendMail(mailArgument.capture());
        Mail mail = mailArgument.getValue();
        assertThat(mail.target()).isEqualTo(target);
        assertThat(mail.content()).contains("회원가입 인증코드입니다.");
        assertThat(mail.title()).isEqualTo("[플레이쿠라운드] 회원가입 인증코드입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"notEmail", "id@domain", "id@domain@something"})
    @DisplayName("이메일 도메인이 올바르지 않으면 NotKUEmailException이 발생한다.")
    void sendAuthEmail_2(String target) {
        assertThatThrownBy(() -> authEmailSendService.sendAuthEmail(target))
                .isInstanceOf(NotKUEmailException.class);
    }

    @Test
    @DisplayName("하루 최대 전송횟수 이상은 메일을 보낼 수 없다.")
    void sendAuthEmail_3() {
        // given
        when(authEmailRepository.countByTargetAndCreatedAtAfter(any(String.class), any(LocalDateTime.class)))
                .thenReturn(3L);

        // when
        assertThatThrownBy(() -> authEmailSendService.sendAuthEmail("email@test.com"))
                .isInstanceOf(SendingLimitExceededException.class);
    }

}