package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.auth.token.dto.TokenDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.request.UserRegisterRequest;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserRegisterResponse;
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
    public UserRegisterResponse registerUser(UserRegisterRequest registerRequest) {
        validateDuplicateEmail(registerRequest.getEmail());
        validateDuplicateNickName(registerRequest.getNickname());

        User user = userRepository.save(registerRequest.toEntity(Role.ROLE_USER));
        TokenDto tokenDto = userLoginService.login(user.getEmail());
        return UserRegisterResponse.from(tokenDto);
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
