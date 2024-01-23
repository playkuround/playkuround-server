package com.playkuround.playkuroundserver.domain.badge.application.art_design;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

import java.time.LocalTime;
import java.util.Set;

public interface ArtAndDesignBadge {

    boolean supports(Set<BadgeType> userBadges, LocalTime now);

    BadgeType getBadge();
}
