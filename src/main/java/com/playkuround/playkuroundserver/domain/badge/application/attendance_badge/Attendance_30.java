package com.playkuround.playkuroundserver.domain.badge.application.attendance_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.domain.User;

import java.util.Set;

public class Attendance_30 implements AttendanceBadge {

    Attendance_30() {
    }

    @Override
    public boolean supports(Set<BadgeType> userBadgeSet, User user) {
        BadgeType badgeType = getBadgeType();
        return !userBadgeSet.contains(badgeType) && user.getAttendanceDays() >= 30;
    }

    @Override
    public BadgeType getBadgeType() {
        return BadgeType.ATTENDANCE_30;
    }
}
