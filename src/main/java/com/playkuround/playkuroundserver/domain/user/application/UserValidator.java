package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.exception.UserEmailDuplicationException;
import com.playkuround.playkuroundserver.domain.user.exception.UserNicknameDuplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserValidator {

    private final UserRepository userRepository;

    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException();
        }
    }

    public void validateDuplicateNickName(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new UserNicknameDuplicationException();
        }
    }

}
