package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.dto.AuthVerifyEmailDto;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthCodeExpiredException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.AuthEmailNotFoundException;
import com.playkuround.playkuroundserver.domain.auth.email.exception.NotMatchAuthCodeException;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthEmailVerifyService {

    private final AuthEmailRepository authEmailRepository;
    private final UserRepository userRepository;
    private final TokenManager tokenManager;
    private final TokenService tokenService;

    public AuthVerifyEmailDto.Response verifyAuthEmail(String code, String email) {
        AuthEmail authEmail = authEmailRepository.findFirstByTargetOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new AuthEmailNotFoundException(email));

        if (authEmail.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new AuthCodeExpiredException();
        }
        if (!authEmail.getCode().equals(code)) {
            throw new NotMatchAuthCodeException();
        }

        authEmailRepository.delete(authEmail);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            TokenDto tokenDto = tokenManager.createTokenDto(user.getEmail());
            tokenService.updateRefreshToken(user);
            return AuthVerifyEmailDto.Response.of(tokenDto);
        }
        else return new AuthVerifyEmailDto.Response();
    }

}
