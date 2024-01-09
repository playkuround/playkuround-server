package com.playkuround.playkuroundserver.domain.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private AttendanceRegisterResponse() {
    }

    public static AttendanceRegisterResponse from(NewlyRegisteredBadge newlyRegisteredBadge) {
        AttendanceRegisterResponse response = new AttendanceRegisterResponse();
        newlyRegisteredBadge.getNewlyBadges()
                .forEach(badgeInfo -> response.addBadge(BadgeType.valueOf(badgeInfo.getName())));
        return response;
    }

    public void addBadge(BadgeType badgeType) {
        this.newBadges.add(new BadgeInfo(badgeType.name(), badgeType.getDescription()));
    }

    @Getter
    @AllArgsConstructor
    public static class BadgeInfo {
        @JsonProperty("name")
        @Schema(description = "뱃지 이름", example = "ATTENDANCE_7", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @JsonProperty("description")
        @Schema(description = "뱃지 설명", example = "7일 연속 출석", requiredMode = Schema.RequiredMode.REQUIRED)
        private String description;
    }
}
