package com.playkuround.playkuroundserver.domain.badge.application.attendance_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

class Attendance_5 implements AttendanceBadge {

    Attendance_5() {
    }

    @Override
    public boolean supports(int attendanceDays) {
        return attendanceDays >= 5;
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_5;
    }
}
