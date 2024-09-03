package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthVerifyEmailResult;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthVerifyTokenResult;
import com.playkuround.playkuroundserver.domain.auth.email.dto.TokenDtoResult;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthCodeExpiredException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthEmailNotFoundException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotMatchAuthCodeException;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Profile("!dev")
public class AuthEmailVerifyServiceImpl implements AuthEmailVerifyService {

    private final TokenService tokenService;
    private final UserLoginService userLoginService;
    private final AuthEmailRepository authEmailRepository;
    private final DateTimeService dateTimeService;

    @Transactional
    public AuthVerifyEmailResult verifyAuthEmail(String code, String email) {
        AuthEmail authEmail = authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(email)
                .orElseThrow(AuthEmailNotFoundException::new);

        validateEmailAndCode(authEmail, code);
        authEmail.changeInvalidate();

        try {
            TokenDto tokenDto = userLoginService.login(email);
            return new TokenDtoResult(tokenDto);
        } catch (UserNotFoundException e) {
            AuthVerifyToken authVerifyToken = tokenService.saveAuthVerifyToken();
            return new AuthVerifyTokenResult(authVerifyToken.getAuthVerifyToken());
        }
    }

    private void validateEmailAndCode(AuthEmail authEmail, String code) {
        if (!authEmail.isValidate()) {
            throw new AuthEmailNotFoundException();
        }
        if (authEmail.getExpiredAt().isBefore(dateTimeService.getLocalDateTimeNow())) {
            throw new AuthCodeExpiredException();
        }
        if (!authEmail.getCode().equals(code)) {
            throw new NotMatchAuthCodeException();
        }
    }
}
