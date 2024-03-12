package com.playkuround.playkuroundserver.domain.badge.application.attendance_badge;

import lombok.Getter;

import java.util.List;

public class AttendanceBadgeList {

    @Getter
    private final static List<AttendanceBadge> attendanceBadges = List.of(
            new Attendance_1(),
            new Attendance_5(),
            new Attendance_10(),
            new Attendance_30(),
            new Attendance_100()
    );

    private AttendanceBadgeList() {
    }
}
