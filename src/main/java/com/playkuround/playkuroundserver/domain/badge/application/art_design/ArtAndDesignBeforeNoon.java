package com.playkuround.playkuroundserver.domain.badge.application.art_design;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

import java.time.LocalTime;
import java.util.Set;

public class ArtAndDesignBeforeNoon implements ArtAndDesignBadge {

    protected ArtAndDesignBeforeNoon() {
    }

    @Override
    public boolean supports(Set<BadgeType> userBadges, LocalTime now) {
        if (!userBadges.contains(BadgeType.COLLEGE_OF_ART_AND_DESIGN_BEFORE_NOON)) {
            return LocalTime.of(9, 0).isBefore(now) && LocalTime.of(12, 0).isAfter(now);
        }
        return false;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_ART_AND_DESIGN_BEFORE_NOON;
    }
}
