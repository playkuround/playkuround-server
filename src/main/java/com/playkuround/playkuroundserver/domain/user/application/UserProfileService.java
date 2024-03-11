package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.HighestScore;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserNotification;
import com.playkuround.playkuroundserver.global.util.BadWordFilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public List<UserNotification> getNotification(User user) {
        String str_notification = user.getNotification();
        if (str_notification == null) {
            return new ArrayList<>();
        }
        user.clearNotification();
        userRepository.save(user);
        return convertToUserNotificationList(str_notification);
    }

    private List<UserNotification> convertToUserNotificationList(String str_notification) {
        return Arrays.stream(str_notification.split("@"))
                .map(notifications -> notifications.split("#"))
                .filter(nameAndDescription -> nameAndDescription.length == 2)
                .map(nameAndDescription -> new UserNotification(nameAndDescription[0], nameAndDescription[1]))
                .toList();
    }
}
