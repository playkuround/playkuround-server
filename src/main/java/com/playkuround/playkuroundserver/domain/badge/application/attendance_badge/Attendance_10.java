package com.playkuround.playkuroundserver.domain.badge.application.attendance_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

class Attendance_10 implements AttendanceBadge {

    Attendance_10() {
    }

    @Override
    public boolean supports(int attendanceDays) {
        return attendanceDays >= 10;
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_10;
    }
}
