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
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    public UserProfileResponse getUserProfile(User user) {
        return UserProfileResponse.from(user);
    }

    public boolean checkDuplicateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public UserGameHighestScoreResponse getUserGameHighestScore(User user) {
        return UserGameHighestScoreResponse.from(user.getHighestScore());
    }

    @Transactional
    public List<UserNotificationResponse> getNotification(User user) {
        List<UserNotificationResponse> notificationResponses = new ArrayList<>();

        String str_notification = user.getNotification();
        if (str_notification == null) {
            return notificationResponses;
        }

        String[] notifications = str_notification.split("@");
        for (String notification : notifications) {
            String[] nameAndDescription = notification.split("#");
            UserNotificationResponse notificationResponse
                    = UserNotificationResponse.from(nameAndDescription[0], nameAndDescription[1]);
            notificationResponses.add(notificationResponse);
        }

        user.clearNotification();
        return notificationResponses;
    }
}
