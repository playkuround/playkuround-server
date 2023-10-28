package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthEmailSendDto;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotKUEmailException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.SendingLimitExceededException;
import com.playkuround.playkuroundserver.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthEmailSendService {

    private final EmailService emailService;
    private final AuthEmailRepository authEmailRepository;

    @Value("${authentication.email.domain}")
    private String emailDomain;

    @Value("${authentication.email.max-send-count}")
    private Long maxSendingCount;

    @Value("${authentication.email.code-length}")
    private Long codeLength;

    @Transactional
    public AuthEmailSendDto.Response sendAuthEmail(AuthEmailSendDto.Request requestDto) {
        String target = requestDto.getTarget();
        validateEmailDomain(target);
        Long sendingCount = validateSendingCount(target);

        String title = "[플레이쿠라운드] 회원가입 인증코드입니다.";
        String code = createCode();
        String content = createContent(code);
        emailService.sendMessage(target, title, content);

        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);
        AuthEmail authEmail = AuthEmail.createAuthEmail(target, code, expiredAt);
        authEmailRepository.save(authEmail);

        return new AuthEmailSendDto.Response(expiredAt, sendingCount + 1);
    }

    private void validateEmailDomain(String target) {
        String[] requestSplit = target.split("@");
        if (requestSplit.length != 2 || requestSplit[1].equals(emailDomain)) {
            throw new NotKUEmailException();
        }
    }

    private Long validateSendingCount(String target) {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        Long sendingCount = authEmailRepository.countByTargetAndCreatedAtAfter(target, today);
        if (sendingCount >= maxSendingCount) {
            throw new SendingLimitExceededException();
        }
        return sendingCount;
    }

    private String createCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            codeBuilder.append(randomChar);
        }

        return codeBuilder.toString();
    }

    private String createContent(String code) {
        String content = "<div>" +
                "<h2>안녕하세요, 플레이쿠라운드입니다.</h1>" +
                "<div font-family:verdana'>" +
                "<p>아래 인증코드를 회원가입 창으로 돌아가 입력해주세요.<p>" +
                "<p>회원가입 인증코드입니다.<p>" +
                "<div style='font-size:130%'>" +
                "<strong>" +
                code +
                "</strong>" +
                "<div>" +
                "<br> " +
                "</div>" +
                "<br>" +
                "</div>";

        return content;
    }
}
