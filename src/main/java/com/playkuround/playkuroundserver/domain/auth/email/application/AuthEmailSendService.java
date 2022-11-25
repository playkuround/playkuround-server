package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthEmailSendDto;
import com.playkuround.playkuroundserver.domain.auth.email.exception.SendingLimitExceededException;
import com.playkuround.playkuroundserver.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthEmailSendService {

    private final EmailService emailService;
    private final AuthEmailRepository authEmailRepository;

    @Transactional
    public AuthEmailSendDto.Response sendAuthEmail(AuthEmailSendDto.Request requestDto) {
        String target = requestDto.getTarget();

        Long sendingCount = validateSendingCount(target);

        String title = "[플레이쿠라운드] 회원가입 인증코드입니다.";
        String code = createCode();
        String content = createContent(code);

        emailService.sendMessage(target, title, content);

        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);
        AuthEmail authEmail = AuthEmail.createAuthEmail(target, code, expiredAt);
        authEmailRepository.save(authEmail);

        // 현재 전송 횟수 포함하여 반환
        return AuthEmailSendDto.Response.of(expiredAt, sendingCount + 1);
    }

    /**
     * 하루에 인증 메일 전송은 5회까지 가능합니다.
     * 5회 초과 시 예외를 던집니다.
     *
     * @param target 인증 메일 전송 대상
     */
    private Long validateSendingCount(String target) {
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
        Long sendingCount = authEmailRepository.countByTargetAndCreatedAtAfter(target, today);
        if (sendingCount >= 5) {
            throw new SendingLimitExceededException();
        }

        return sendingCount;
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

    private static String createCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int type = random.nextInt(3);

            switch (type) {
                case 0:
                    // 알파벳 소문자(a~z)
                    code.append((char) (int) (random.nextInt(26) + 97));
                    break;
                case 1:
                    // 알파벳 대문자(A~Z)
                    code.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    // 정수 0~9
                    code.append(random.nextInt(10));
                    break;
            }
        }

        return code.toString();
    }

}
