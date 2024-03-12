package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.domain.user.exception.UserEmailDuplicationException;
import com.playkuround.playkuroundserver.domain.user.exception.UserNicknameDuplicationException;
import com.playkuround.playkuroundserver.domain.user.exception.UserNicknameUnavailableException;
import com.playkuround.playkuroundserver.global.util.BadWordFilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRegisterService {

    private final UserRepository userRepository;
    private final UserLoginService userLoginService;

    @Transactional
    public TokenDto registerUser(UserRegisterDto userRegisterDto) {
        validateDuplicateEmail(userRegisterDto.email());
        validateDuplicateNickName(userRegisterDto.nickname());

        User user = User.create(userRegisterDto.email(), userRegisterDto.nickname(), userRegisterDto.major(), Role.ROLE_USER);
        userRepository.save(user);

        return userLoginService.login(user.getEmail());
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
        if (BadWordFilterUtils.check(nickname)) {
            throw new UserNicknameUnavailableException();
        }
    }

}
