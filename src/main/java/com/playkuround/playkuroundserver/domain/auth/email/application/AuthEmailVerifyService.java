package com.playkuround.playkuroundserver.domain.auth.email.application;

import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthEmailRepository;
import com.playkuround.playkuroundserver.domain.auth.email.dao.AuthVerifyTokenRepository;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthEmail;
import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthVerifyToken;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthEmailVerifyService {

    private final TokenManager tokenManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final AuthEmailRepository authEmailRepository;
    private final AuthVerifyTokenRepository authVerifyTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("${token.authverify-token-expiration-seconds}")
    private Integer authVerifyTokenExpirationSeconds;

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

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(user.getEmail(), null);
            Authentication authentication = authenticationManagerBuilder.getObject()
                    .authenticate(authenticationToken);

            TokenDto tokenDto = tokenManager.createTokenDto(authentication);
            tokenService.updateRefreshToken(user);
            return AuthVerifyEmailDto.Response.from(tokenDto);
        }
        else {
            AuthVerifyToken authVerifyToken = new AuthVerifyToken(authVerifyTokenExpirationSeconds);
            authVerifyTokenRepository.save(authVerifyToken);
            return AuthVerifyEmailDto.Response.createByAuthVerifyToken(authVerifyToken.getAuthVerifyToken());
        }
    }

}
