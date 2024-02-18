package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

import java.util.Set;

public interface SpecialDayBadge {

    boolean supports(Set<BadgeType> userBadgeSet);

    BadgeType getBadgeType();
}
