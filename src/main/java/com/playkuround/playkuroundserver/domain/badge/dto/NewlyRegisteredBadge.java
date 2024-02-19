package com.playkuround.playkuroundserver.domain.badge.dto;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
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

    public record BadgeInfo(String name, String description) {
    }
}
