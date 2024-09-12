package com.playkuround.playkuroundserver.domain.badge.application.attendance_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

class Attendance_1 implements AttendanceBadge {

    Attendance_1() {
    }

    @Override
    public boolean supports(int attendanceDays) {
        return attendanceDays >= 1;
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_1;
    }
}
