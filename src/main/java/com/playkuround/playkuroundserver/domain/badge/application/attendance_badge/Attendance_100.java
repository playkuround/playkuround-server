package com.playkuround.playkuroundserver.domain.badge.application.attendance_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

class Attendance_100 implements AttendanceBadge {

    Attendance_100() {
    }

    @Override
    public boolean supports(int attendanceDays) {
        return attendanceDays >= 100;
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_100;
    }
}
