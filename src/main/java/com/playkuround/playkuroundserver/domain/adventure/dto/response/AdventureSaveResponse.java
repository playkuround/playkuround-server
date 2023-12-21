package com.playkuround.playkuroundserver.domain.adventure.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AdventureSaveResponse {

    @Setter
    private Integer correctionScore;
    private List<BadgeInfo> newBadges = new ArrayList<>();

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
