package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.HighestScore;
import com.playkuround.playkuroundserver.domain.user.domain.Notification;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.BadWordFilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final Pattern nicknamePattern = Pattern.compile("^[0-9a-zA-Z가-힣]{2,8}$");

    @Transactional(readOnly = true)
    public boolean isAvailableNickname(String nickname) {
        return nicknamePattern.matcher(nickname).matches() &&
                !BadWordFilterUtils.check(nickname) &&
                !userRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public HighestScore getUserGameHighestScore(User user) {
        return user.getHighestScore();
    }

    @Transactional
    public List<Notification> getNotification(User user) {
        Set<Notification> notificationSet = user.getNotification();
        if (notificationSet == null || notificationSet.isEmpty()) {
            return new ArrayList<>();
        }

        List<Notification> userNotifications = notificationSet.stream().toList();
        user.clearNotification();
        userRepository.save(user);

        return userNotifications;
    }
}
