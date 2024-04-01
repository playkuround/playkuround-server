package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;

import java.util.Set;

public class ArborDayBadge implements SpecialDayBadge {

    ArborDayBadge() {
    }

    @Override
    public boolean supports(Set<BadgeType> userBadgeSet) {
        BadgeType badgeType = getBadgeType();
        return DateTimeUtils.isTodayArborDay() && !userBadgeSet.contains(badgeType);
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_ARBOR_DAY;
    }
}
