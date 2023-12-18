package com.playkuround.playkuroundserver.domain.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
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
        String name;

        @JsonProperty("description")
        String description;
    }
}
