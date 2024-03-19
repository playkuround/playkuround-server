package com.playkuround.playkuroundserver.infra.email;

import com.playkuround.playkuroundserver.infra.email.exception.EmailSendFailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async("mailExecutor")
    public void sendMail(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.addRecipients(MimeMessage.RecipientType.TO, mail.target());
            message.setSubject(mail.title());
            message.setText(mail.content(), mail.encoding(), mail.subtype());
            message.setFrom(new InternetAddress(mail.fromAddress(), mail.fromPersonal()));

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendFailException();
        }
    }

}
