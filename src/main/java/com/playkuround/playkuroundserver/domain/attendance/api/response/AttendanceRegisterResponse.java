package com.playkuround.playkuroundserver.domain.attendance.api.response;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class AttendanceRegisterResponse {

    private final List<BadgeInfo> newBadges;

    private AttendanceRegisterResponse(List<BadgeType> newlyRegisteredBadge) {
        this.newBadges = newlyRegisteredBadge.stream()
                .map(badgeType -> new BadgeInfo(badgeType.name(), badgeType.getDescription()))
                .toList();
    }

    public static AttendanceRegisterResponse from(List<BadgeType> newlyRegisteredBadge) {
        return new AttendanceRegisterResponse(newlyRegisteredBadge);
    }

    @Getter
    @AllArgsConstructor
    public static class BadgeInfo {

        @Schema(description = "배지 이름", example = "ATTENDANCE_7", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @Schema(description = "배지 설명", example = "7일 연속 출석", requiredMode = Schema.RequiredMode.REQUIRED)
        private String description;
    }
}
