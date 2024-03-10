package com.playkuround.playkuroundserver.domain.user.dto.response;

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

    public static UserNotificationResponse of(String name, String notification) {
        return new UserNotificationResponse(name, notification);
    }

    public static List<UserNotificationResponse> from(NotificationEnum notificationEnum) {
        return List.of(new UserNotificationResponse(notificationEnum.name, notificationEnum.description));
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
