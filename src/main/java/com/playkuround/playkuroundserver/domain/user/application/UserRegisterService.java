package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.application.TokenManager;
import com.playkuround.playkuroundserver.domain.auth.token.application.TokenService;
import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.domain.user.exception.UserEmailDuplicationException;
import com.playkuround.playkuroundserver.domain.user.exception.UserNicknameDuplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRegisterService {

    private final UserRepository userRepository;
    private final TokenManager tokenManager;
    private final TokenService tokenService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserRegisterDto.Response registerUser(UserRegisterDto.Request registerRequest) {
        validateDuplicateEmail(registerRequest.getEmail());
        validateDuplicateNickName(registerRequest.getNickname());

        User user = userRepository.save(registerRequest.toEntity(Role.ROLE_USER));

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), null);
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        TokenDto tokenDto = tokenManager.createTokenDto(authentication);
        tokenService.registerRefreshToken(user, tokenDto.getRefreshToken());

        return UserRegisterDto.Response.from(tokenDto);
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException();
        }
    }

    private void validateDuplicateNickName(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserNicknameDuplicationException();
        }
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

}
