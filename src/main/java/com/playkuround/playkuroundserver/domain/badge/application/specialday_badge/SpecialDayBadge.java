package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

import java.time.LocalDate;

public interface SpecialDayBadge {

    boolean supports(LocalDate localDate);

    BadgeType getBadgeType();
}
