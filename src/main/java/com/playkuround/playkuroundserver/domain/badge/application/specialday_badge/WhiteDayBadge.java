package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;

import java.time.LocalDate;
import java.util.Set;

public class WhiteDayBadge implements SpecialDayBadge {

    WhiteDayBadge() {
    }

    @Override
    public boolean supports(Set<BadgeType> userBadgeSet, LocalDate localDate) {
        BadgeType badgeType = getBadgeType();
        return DateTimeUtils.isWhiteDay(localDate) && !userBadgeSet.contains(badgeType);
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_WHITE_DAY;
    }
}
