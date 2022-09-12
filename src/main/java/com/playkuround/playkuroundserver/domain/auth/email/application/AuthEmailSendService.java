package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthEmailSendDto;
import com.playkuround.playkuroundserver.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthEmailSendService {

    private final EmailService emailService;
    private final AuthEmailRepository authEmailRepository;

    @Transactional
    public AuthEmailSendDto.Response sendAuthEmail(AuthEmailSendDto.Request requestDto) {
        String target = requestDto.getTarget();
        String title = "[플레이쿠라운드] 회원가입 인증코드입니다.";
        String code = createCode();
        String content = createContent(code);

        emailService.sendMessage(target, title, content);

        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);
        AuthEmail authEmail = AuthEmail.createAuthEmail(target, code, expiredAt);
        authEmailRepository.save(authEmail);

        return AuthEmailSendDto.Response.of(expiredAt);
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
                    code.append((char) (int)(random.nextInt(26) + 97));
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