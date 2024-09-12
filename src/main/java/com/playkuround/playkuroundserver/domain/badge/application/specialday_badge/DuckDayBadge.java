package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;

import java.time.LocalDate;

class DuckDayBadge implements SpecialDayBadge {

    DuckDayBadge() {
    }

    @Override
    public boolean supports(LocalDate localDate) {
        return DateTimeUtils.isDuckDay(localDate);
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_DUCK_DAY;
    }
}
