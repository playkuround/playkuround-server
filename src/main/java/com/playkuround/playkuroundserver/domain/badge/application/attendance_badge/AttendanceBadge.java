package com.playkuround.playkuroundserver.domain.badge.application.attendance_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

public interface AttendanceBadge {

    boolean supports(int attendanceDays);

    BadgeType getBadgeType();
}
