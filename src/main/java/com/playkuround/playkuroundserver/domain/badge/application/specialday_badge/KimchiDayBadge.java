package com.playkuround.playkuroundserver.domain.badge.application.specialday_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;

import java.time.LocalDate;

class KimchiDayBadge implements SpecialDayBadge {

    KimchiDayBadge() {
    }

    @Override
    public boolean supports(LocalDate localDate) {
        return DateTimeUtils.isKimchiDay(localDate);
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_KIMCHI_DAY;
    }
}
