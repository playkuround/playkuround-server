package com.playkuround.playkuroundserver.domain.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class AttendanceRegisterResponse {
    private List<BadgeInfo> newBadges;

    public void addBadge(BadgeType badgeType) {
        this.newBadges.add(new BadgeInfo(badgeType.name(), badgeType.getDescription()));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private static class BadgeInfo {
        @JsonProperty("name")
        @Schema(description = "뱃지 이름", example = "ATTENDANCE_7", requiredMode = Schema.RequiredMode.REQUIRED)
        String name;

        @JsonProperty("description")
        @Schema(description = "뱃지 설명", example = "7일 연속 출석", requiredMode = Schema.RequiredMode.REQUIRED)
        String description;
    }
}
