package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;

import java.time.LocalDate;

class ChildrenDayBadge implements SpecialDayBadge {

    ChildrenDayBadge() {
    }

    @Override
    public boolean supports(LocalDate localDate) {
        return DateTimeUtils.isChildrenDay(localDate);
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_CHILDREN_DAY;
    }
}
