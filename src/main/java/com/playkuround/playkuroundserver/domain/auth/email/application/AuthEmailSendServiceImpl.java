package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthEmailInfo;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotKUEmailException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.SendingLimitExceededException;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.infra.email.EmailService;
import com.playkuround.playkuroundserver.infra.email.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
@RequiredArgsConstructor
@Profile("!dev")
public class AuthEmailSendServiceImpl implements AuthEmailSendService {

    private final EmailService emailService;
    private final AuthEmailRepository authEmailRepository;
    private final DateTimeService dateTimeService;
    private final TemplateEngine templateEngine;

    @Value("${authentication.email.domain}")
    private String emailDomain;

    @Value("${authentication.email.title}")
    private String emailTitle;

    @Value("${authentication.email.max-send-count}")
    private Long maxSendingCount;

    @Value("${authentication.email.code-length}")
    private Long codeLength;

    @Value("${authentication.email.code-expiration-seconds}")
    private Long codeExpirationSeconds;

    @Transactional
    @Override
    public AuthEmailInfo sendAuthEmail(String target) {
        validateEmailDomain(target);
        long sendingCount = validateSendingCount(target);

        CodeGenerator codeGenerator = new CodeGenerator();
        String authenticationCode = codeGenerator.generateCode(EnumSet.of(CodeGenerator.CodeType.NUMBER), codeLength);
        LocalDateTime expiredAt = saveAuthEmail(target, authenticationCode);
        sendEmail(target, authenticationCode);

        return new AuthEmailInfo(expiredAt, sendingCount + 1);
    }

    private void validateEmailDomain(String target) {
        String[] requestSplit = target.split("@");
        if (requestSplit.length != 2 || !requestSplit[1].equals(emailDomain)) {
            throw new NotKUEmailException();
        }
    }

    private long validateSendingCount(String target) {
        LocalDate today = dateTimeService.getLocalDateNow();
        LocalDateTime startOfToday = today.atStartOfDay();
        long sendingCount = authEmailRepository.countByTargetAndCreatedAtGreaterThanEqual(target, startOfToday);
        if (sendingCount >= maxSendingCount) {
            throw new SendingLimitExceededException();
        }
        return sendingCount;
    }

    private LocalDateTime saveAuthEmail(String target, String authenticationCode) {
        LocalDateTime now = dateTimeService.getLocalDateTimeNow();
        LocalDateTime expiredAt = now.plusSeconds(codeExpirationSeconds);

        AuthEmail authEmail = AuthEmail.createAuthEmail(target, authenticationCode, expiredAt);
        authEmailRepository.save(authEmail);
        return expiredAt;
    }

    private void sendEmail(String target, String authenticationCode) {
        Context context = new Context();
        context.setVariable("code", authenticationCode);
        String content = templateEngine.process("mail-template", context);

        Mail mail = new Mail(target, emailTitle, content);
        emailService.sendMail(mail);
    }
}
