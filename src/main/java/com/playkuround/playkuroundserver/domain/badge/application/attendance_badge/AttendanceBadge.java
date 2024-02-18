package com.playkuround.playkuroundserver.domain.badge.application.attendance_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.domain.User;

import java.util.Set;

public interface AttendanceBadge {

    boolean supports(Set<BadgeType> userBadgeSet, User user);

    BadgeType getBadgeType();
}
