package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.exception.DuplicateAttendanceException;
import com.playkuround.playkuroundserver.domain.attendance.exception.InvalidAttendanceLocationException;
import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.score.application.TotalScoreService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;
import com.playkuround.playkuroundserver.global.util.LocationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AttendanceRegisterService {

    private final BadgeService badgeService;
    private final UserRepository userRepository;
    private final TotalScoreService totalScoreService;
    private final AttendanceRepository attendanceRepository;
    private final DateTimeService dateTimeService;
    private final long attendanceScore = 10;

    @Transactional
    public NewlyRegisteredBadge registerAttendance(User user, Location location) {
        validateAttendance(user, location);

        saveAttendance(user, location);
        updateUserAttendanceDay(user);

        totalScoreService.incrementTotalScore(user, attendanceScore);

        return badgeService.updateNewlyAttendanceBadges(user);
    }

    private void validateAttendance(User user, Location location) {
        validateLocation(location);
        validateDuplicateAttendance(user);
    }

    private void validateLocation(Location location) {
        boolean isLocatedInKU = LocationUtils.isLocatedInKU(location);
        if (!isLocatedInKU) {
            throw new InvalidAttendanceLocationException();
        }
    }

    private void validateDuplicateAttendance(User user) {
        if (attendanceRepository.existsByUserAndAttendanceDateTimeAfter(user, LocalDate.now().atStartOfDay())) {
            throw new DuplicateAttendanceException();
        }
    }

    private void saveAttendance(User user, Location location) {
        Attendance attendance = Attendance.of(user, location, dateTimeService.now());
        attendanceRepository.save(attendance);
    }

    private void updateUserAttendanceDay(User user) {
        user.increaseAttendanceDay();
        userRepository.save(user);
    }

}
