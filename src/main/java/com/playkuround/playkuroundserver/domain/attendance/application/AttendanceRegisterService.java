package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.dto.response.AttendanceRegisterResponse;
import com.playkuround.playkuroundserver.domain.attendance.exception.DuplicateAttendanceException;
import com.playkuround.playkuroundserver.domain.attendance.exception.InvalidAttendanceLocationException;
import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
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
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Transactional
    public AttendanceRegisterResponse registerAttendance(User user, Location location) {
        validateAttendance(user, location);

        Attendance attendance = Attendance.createAttendance(user, location);
        attendanceRepository.save(attendance);

        user.increaseAttendanceDay();
        userRepository.save(user);

        NewlyRegisteredBadge newlyRegisteredBadge = badgeService.updateNewlyAttendanceBadges(user);
        return AttendanceRegisterResponse.from(newlyRegisteredBadge);
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
        if (attendanceRepository.existsByUserAndCreatedAtAfter(user, LocalDate.now().atStartOfDay())) {
            throw new DuplicateAttendanceException();
        }
    }


}
