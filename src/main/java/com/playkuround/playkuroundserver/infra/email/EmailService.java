package com.playkuround.playkuroundserver.infra.email;

import com.playkuround.playkuroundserver.infra.email.exception.EmailSendFailException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendMessage(String target, String title, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.addRecipients(MimeMessage.RecipientType.TO, target);
            message.setSubject(title);
            message.setText(content, "UTF-8", "HTML");
            message.setFrom(new InternetAddress("playkuround@gmail.com", "플레이쿠라운드"));

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendFailException();
        }
    }

}
