package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.response.AuthVerifyEmailResponse;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthCodeExpiredException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthEmailNotFoundException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotMatchAuthCodeException;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthEmailVerifyService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserLoginService userLoginService;
    private final AuthEmailRepository authEmailRepository;

    @Transactional
    public AuthVerifyEmailResponse verifyAuthEmail(String code, String email) {
        AuthEmail authEmail = authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(email)
                .orElseThrow(AuthEmailNotFoundException::new);

        validateEmailAndCode(authEmail, code);
        authEmail.changeInvalidate();

        boolean existsUser = userRepository.existsByEmail(email);
        if (existsUser) {
            TokenDto tokenDto = userLoginService.login(email);
            return AuthVerifyEmailResponse.fromTokenDto(tokenDto);
        }
        else {
            AuthVerifyToken authVerifyToken = tokenService.registerAuthVerifyToken();
            return AuthVerifyEmailResponse.createByAuthVerifyToken(authVerifyToken.getAuthVerifyToken());
        }
    }

    private void validateEmailAndCode(AuthEmail authEmail, String code) {
        if (!authEmail.isValidate()) {
            throw new AuthEmailNotFoundException();
        }
        if (authEmail.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new AuthCodeExpiredException();
        }
        if (!authEmail.getCode().equals(code)) {
            throw new NotMatchAuthCodeException();
        }
    }
}
