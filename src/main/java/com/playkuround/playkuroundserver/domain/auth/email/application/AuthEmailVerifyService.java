package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.exception.AuthCodeExpiredException;
import com.playkuround.playkuroundserver.domain.auth.exception.AuthEmailNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthEmailVerifyService {

    private final AuthEmailRepository authEmailRepository;

    public boolean verifyAuthEmail(String code, String email) {
        AuthEmail authEmail = authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new AuthEmailNotFoundException(email));

        if (authEmail.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new AuthCodeExpiredException();
        }

        return authEmail.getCode().equals(code);
    }

}
