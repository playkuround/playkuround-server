package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceRegisterDto;
import com.playkuround.playkuroundserver.domain.attendance.exception.DuplicateAttendanceException;
import com.playkuround.playkuroundserver.domain.attendance.exception.InvalidAttendanceLocationException;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.LocationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceRegisterService {

    private final AttendanceRepository attendanceRepository;
    private final BadgeRepository badgeRepository;
    private final UserFindDao userFindDao;

    @Transactional
    public void registerAttendance(UserDetails userDetails, AttendanceRegisterDto.Request registerRequest) {
        User user = userFindDao.findByUserDetails(userDetails);
        validateAttendance(user, registerRequest);
        Attendance attendance = registerRequest.toEntity(user);

        attendanceRepository.save(attendance);
        updateNewBadges(user);
    }

    private void updateNewBadges(User user) {
        boolean hasAttendance_1 = false, hasAttendance_3 = false, hasAttendance_7 = false;
        boolean hasAttendance_30 = false, hasAttendance_100 = false, hasAttendance_Foundation_Day = false;

        List<Badge> badges = badgeRepository.findByUser(user);
        for (Badge badge : badges) {
            BadgeType badgeType = badge.getBadgeType();
            if (badgeType.name().equals("ATTENDANCE_1")) hasAttendance_1 = true;
            else if (badgeType.name().equals("ATTENDANCE_3")) hasAttendance_3 = true;
            else if (badgeType.name().equals("ATTENDANCE_7")) hasAttendance_7 = true;
            else if (badgeType.name().equals("ATTENDANCE_30")) hasAttendance_30 = true;
            else if (badgeType.name().equals("ATTENDANCE_100")) hasAttendance_100 = true;
            else if (badgeType.name().equals("ATTENDANCE_FOUNDATION_DAY")) hasAttendance_Foundation_Day = true;
        }

        if (!hasAttendance_1) badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_1));
        else if (!hasAttendance_3) {
            if (isEligibleForAttendanceBadge(user, 3L)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_3));
            }
        }
        else if (!hasAttendance_7) {
            if (isEligibleForAttendanceBadge(user, 7L)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_7));
            }
        }
        else if (!hasAttendance_30) {
            if (isEligibleForAttendanceBadge(user, 30L)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_30));
            }
        }
        else if (!hasAttendance_100) {
            if (isEligibleForAttendanceBadge(user, 100L)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_100));
            }
        }
        if (isTodayFoundationDay() && !hasAttendance_Foundation_Day) {
            badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_FOUNDATION_DAY));
        }
    }

    private boolean isEligibleForAttendanceBadge(User user, Long consecutiveDays) {
        LocalDateTime afterDate =
                LocalDateTime.of(LocalDate.now().minusDays(consecutiveDays), LocalTime.of(0, 0, 0));
        return attendanceRepository.countByUserAndCreatedAtAfter(user, afterDate).equals(consecutiveDays);
    }

    private boolean isTodayFoundationDay() {
        return LocalDate.now().getMonth().getValue() == 5 && LocalDate.now().getDayOfMonth() == 15;
    }

    private void validateAttendance(User user, AttendanceRegisterDto.Request registerRequest) {
        // 건대에 있는지 검증
        double latitude = registerRequest.getLatitude();
        double longitude = registerRequest.getLongitude();
        validateLocation(latitude, longitude);

        // 이미 출석했는지 검증
        validateDuplicateAttendance(user);
    }

    private void validateDuplicateAttendance(User user) {
        if (attendanceRepository.existsByUserAndCreatedAtAfter(user, LocalDate.now().atStartOfDay())) {
            throw new DuplicateAttendanceException();
        }
    }

    private void validateLocation(double latitude, double longitude) {
        boolean isLocatedInKU = LocationUtils.isLocatedInKU(latitude, longitude);

        if (!isLocatedInKU) {
            throw new InvalidAttendanceLocationException();
        }
    }

}
