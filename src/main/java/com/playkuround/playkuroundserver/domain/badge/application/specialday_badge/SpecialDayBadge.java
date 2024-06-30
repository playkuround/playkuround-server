package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

import java.time.LocalDate;
import java.util.Set;

public interface SpecialDayBadge {

    boolean supports(Set<BadgeType> userBadgeSet, LocalDate localDate);

    BadgeType getBadgeType();
}
