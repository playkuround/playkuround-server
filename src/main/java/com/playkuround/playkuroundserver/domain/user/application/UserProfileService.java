package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserGameHighestScoreResponse;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserNotificationResponse;
import com.playkuround.playkuroundserver.domain.user.dto.response.UserProfileResponse;
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
    public UserProfileResponse getUserProfile(User user) {
        return UserProfileResponse.from(user);
    }

    @Transactional(readOnly = true)
    public boolean isAvailableNickname(String nickname) {
        if (!nicknamePattern.matcher(nickname).matches()) {
            return false;
        }
        return userRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public UserGameHighestScoreResponse getUserGameHighestScore(User user) {
        return UserGameHighestScoreResponse.from(user.getHighestScore());
    }

    @Transactional
    public List<UserNotificationResponse> getNotification(User user) {
        String str_notification = user.getNotification();
        if (str_notification == null) {
            return new ArrayList<>();
        }
        user.clearNotification();
        userRepository.save(user);
        return convertToUserNotificationList(str_notification);
    }

    private List<UserNotificationResponse> convertToUserNotificationList(String str_notification) {
        return Arrays.stream(str_notification.split("@"))
                .map(notifications -> notifications.split("#"))
                .map(nameAndDescription -> UserNotificationResponse.from(nameAndDescription[0], nameAndDescription[1]))
                .toList();
    }
}
