package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthEmailInfo;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotKUEmailException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.SendingLimitExceededException;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthEmailSendServiceTest {

    @InjectMocks
    private AuthEmailSendServiceImpl authEmailSendService;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthEmailRepository authEmailRepository;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private DateTimeService dateTimeService;

    private final long codeLength = 6L;
    private final long maxSendingCount = 3L;
    private final String emailDomain = "test.com";
    private final long codeExpirationSeconds = 300L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authEmailSendService, "codeLength", codeLength);
        ReflectionTestUtils.setField(authEmailSendService, "maxSendingCount", maxSendingCount);
        ReflectionTestUtils.setField(authEmailSendService, "emailDomain", emailDomain);
        ReflectionTestUtils.setField(authEmailSendService, "codeExpirationSeconds", codeExpirationSeconds);
    }

    @Test
    @DisplayName("숫자로 이루어진 인증 번호가 저장되고, 이메일로 전송된다.")
    void sendAuthEmail_1() {
        // given
        LocalDateTime now = LocalDateTime.of(2024, 9, 3, 13, 0, 0);
        when(dateTimeService.getLocalDateTimeNow()).thenReturn(now);
        when(dateTimeService.getLocalDateNow()).thenReturn(now.toLocalDate());

        String target = "test@" + emailDomain;
        long sendingCount = 0;
        when(authEmailRepository.countByTargetAndCreatedAtGreaterThanEqual(target, now.toLocalDate().atStartOfDay()))
                .thenReturn(sendingCount);

        String content = "email content";
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenReturn(content);

        // when
        AuthEmailInfo result = authEmailSendService.sendAuthEmail(target);

        // then
        assertThat(result.sendingCount()).isEqualTo(sendingCount + 1);
        assertThat(result.expiredAt()).isEqualTo(now.plusSeconds(codeExpirationSeconds));

        ArgumentCaptor<AuthEmail> authEmailArgument = ArgumentCaptor.forClass(AuthEmail.class);
        verify(authEmailRepository, times(1)).save(authEmailArgument.capture());
        AuthEmail authEmail = authEmailArgument.getValue();
        assertThat(authEmail.getTarget()).isEqualTo(target);
        assertThat(authEmail.getCode()).containsPattern("[0-9]{" + codeLength + "}");

        verify(emailService, times(1)).sendMail(new Mail(target, "[플레이쿠라운드] 회원가입 인증코드입니다.", content));
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
        LocalDate today = LocalDate.of(2024, 9, 3);
        when(dateTimeService.getLocalDateNow()).thenReturn(today);

        String target = "email@test.com";
        when(authEmailRepository.countByTargetAndCreatedAtGreaterThanEqual(target, today.atStartOfDay()))
                .thenReturn(maxSendingCount);

        // when & then
        assertThatThrownBy(() -> authEmailSendService.sendAuthEmail(target))
                .isInstanceOf(SendingLimitExceededException.class);
    }

}