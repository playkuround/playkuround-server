package com.playkuround.playkuroundserver.domain.adventure.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AdventureSaveResponse {

    private List<BadgeInfo> newBadges = new ArrayList<>();

    private AdventureSaveResponse(List<BadgeType> badgeTypes) {
        badgeTypes.forEach(it -> this.newBadges.add(new BadgeInfo(it.name(), it.getDescription())));
    }

    public static AdventureSaveResponse from(NewlyRegisteredBadge newlyRegisteredBadge) {
        List<BadgeType> badgeInfoList = newlyRegisteredBadge.getNewlyBadges().stream()
                .map(badgeInfo -> BadgeType.valueOf(badgeInfo.getName()))
                .toList();
        return new AdventureSaveResponse(badgeInfoList);
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
