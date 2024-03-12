package com.playkuround.playkuroundserver.securityPersistence.code;

import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PersistenceService {

    private final UserRepository userRepository;

    public void successIncreaseAttendanceDay(User user) {
        user.increaseAttendanceDay();
        userRepository.save(user);
    }

    public void failIncreaseAttendanceDay(User user) {
        user.increaseAttendanceDay();
    }

    public void rollbackIncreaseAttendanceDay(User user) {
        user.increaseAttendanceDay();
        userRepository.save(user);
        throw new RuntimeException();
    }
}
