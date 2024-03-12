package com.playkuround.playkuroundserver.domain.user.api.response;

import com.playkuround.playkuroundserver.domain.user.dto.UserNotification;
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
        return List.of(new UserNotificationResponse(notificationEnum.name, notificationEnum.description));
    }

    public static List<UserNotificationResponse> from(List<UserNotification> notificationList) {
        return notificationList.stream()
                .map(notification -> new UserNotificationResponse(notification.name(), notification.description()))
                .toList();
    }

    public enum NotificationEnum {
        UPDATE("update", "application is must update"),
        SYSTEM_CHECK("system_check", "system is not available");

        private final String name;
        private final String description;

        NotificationEnum(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
