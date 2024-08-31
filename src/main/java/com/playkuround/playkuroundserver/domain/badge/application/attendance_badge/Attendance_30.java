package com.playkuround.playkuroundserver.domain.badge.application.attendance_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

class Attendance_30 implements AttendanceBadge {

    Attendance_30() {
    }

    @Override
    public boolean supports(int attendanceDays) {
        return attendanceDays >= 30;
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_30;
    }
}
