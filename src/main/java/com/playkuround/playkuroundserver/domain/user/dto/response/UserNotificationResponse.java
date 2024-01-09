package com.playkuround.playkuroundserver.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserNotificationResponse {

    @Schema(description = "알림 이름", example = "new Badge", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "알림 내용", example = "MONTHLY_RANKING_1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    public static UserNotificationResponse from(String name, String notification) {
        return new UserNotificationResponse(name, notification);
    }
}
