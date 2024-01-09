package com.playkuround.playkuroundserver.domain.badge.dto;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NewlyRegisteredBadge {

    private final List<BadgeInfo> newlyBadges = new ArrayList<>();

    public void addBadge(BadgeType badgeType) {
        BadgeInfo badgeInfo = new BadgeInfo(badgeType.name(), badgeType.getDescription());
        newlyBadges.add(badgeInfo);
    }

    @Getter
    @AllArgsConstructor
    public static class BadgeInfo {
        private String name;
        private String description;
    }
}
