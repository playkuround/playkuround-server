package com.playkuround.playkuroundserver.domain.adventure.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class AdventureSaveResponse {

    private final List<BadgeInfo> newBadges;

    private AdventureSaveResponse(NewlyRegisteredBadge newlyRegisteredBadge) {
        this.newBadges = newlyRegisteredBadge.getNewlyBadges().stream()
                .map(badgeInfo -> BadgeType.valueOf(badgeInfo.name()))
                .map(badgeType -> new BadgeInfo(badgeType.name(), badgeType.getDescription()))
                .toList();
    }

    public static AdventureSaveResponse from(NewlyRegisteredBadge newlyRegisteredBadge) {
        return new AdventureSaveResponse(newlyRegisteredBadge);
    }

    @Getter
    @AllArgsConstructor
    public static class BadgeInfo {
        @JsonProperty("name")
        @Schema(description = "배지 이름", example = "ATTENDANCE_7", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @JsonProperty("description")
        @Schema(description = "배지 설명", example = "7일 연속 출석", requiredMode = Schema.RequiredMode.REQUIRED)
        private String description;
    }
}
