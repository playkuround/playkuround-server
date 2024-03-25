package com.playkuround.playkuroundserver.domain.user.api.response;

import com.playkuround.playkuroundserver.domain.user.domain.Notification;
import com.playkuround.playkuroundserver.domain.user.domain.NotificationEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserNotificationResponse {

    @Schema(description = "알림 이름", example = "new_Badge", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "알림 내용", example = "MONTHLY_RANKING_1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    public static List<UserNotificationResponse> from(NotificationEnum notificationEnum) {
        return List.of(new UserNotificationResponse(notificationEnum.getName(), notificationEnum.getDefaultMessage()));
    }

    public static List<UserNotificationResponse> from(List<Notification> notificationList) {
        return notificationList.stream()
                .map(notification -> new UserNotificationResponse(notification.getName(), notification.getDescription()))
                .toList();
    }
}
