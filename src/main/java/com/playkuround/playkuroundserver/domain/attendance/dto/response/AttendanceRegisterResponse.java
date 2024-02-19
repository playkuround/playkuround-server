package com.playkuround.playkuroundserver.domain.attendance.dto.response;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AttendanceRegisterResponse {

    private final List<BadgeInfo> newBadges = new ArrayList<>();

    private AttendanceRegisterResponse(List<BadgeType> badgeTypes) {
        badgeTypes.forEach(it -> this.newBadges.add(new BadgeInfo(it.name(), it.getDescription())));
    }

    public static AttendanceRegisterResponse from(NewlyRegisteredBadge newlyRegisteredBadge) {
        List<BadgeType> badgeInfoList = newlyRegisteredBadge.getNewlyBadges().stream()
                .map(badgeInfo -> BadgeType.valueOf(badgeInfo.name()))
                .toList();
        return new AttendanceRegisterResponse(badgeInfoList);
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
