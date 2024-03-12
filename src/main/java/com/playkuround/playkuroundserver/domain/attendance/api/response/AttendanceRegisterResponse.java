package com.playkuround.playkuroundserver.domain.attendance.api.response;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class AttendanceRegisterResponse {

    private final List<BadgeInfo> newBadges;

    private AttendanceRegisterResponse(NewlyRegisteredBadge newlyRegisteredBadge) {
        this.newBadges = newlyRegisteredBadge.getNewlyBadges().stream()
                .map(badgeInfo -> BadgeType.valueOf(badgeInfo.name()))
                .map(it -> new BadgeInfo(it.name(), it.getDescription()))
                .toList();
    }

    public static AttendanceRegisterResponse from(NewlyRegisteredBadge newlyRegisteredBadge) {
        return new AttendanceRegisterResponse(newlyRegisteredBadge);
    }

    @Getter
    @AllArgsConstructor
    public static class BadgeInfo {
        @Schema(description = "뱃지 이름", example = "ATTENDANCE_7", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @Schema(description = "뱃지 설명", example = "7일 연속 출석", requiredMode = Schema.RequiredMode.REQUIRED)
        private String description;
    }
}
