package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.util.DateUtils;

import java.util.Set;

public class WhiteDayBadge implements SpecialDayBadge {

    WhiteDayBadge() {
    }

    @Override
    public boolean supports(Set<BadgeType> userBadgeSet) {
        BadgeType badgeType = getBadgeType();
        return DateUtils.isTodayWhiteDay() && !userBadgeSet.contains(badgeType);
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_WHITE_DAY;
    }
}
