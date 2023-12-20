package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.dto.request.AttendanceRegisterRequest;
import com.playkuround.playkuroundserver.domain.attendance.dto.response.AttendanceRegisterResponse;
import com.playkuround.playkuroundserver.domain.attendance.exception.DuplicateAttendanceException;
import com.playkuround.playkuroundserver.domain.attendance.exception.InvalidAttendanceLocationException;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.LocationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceRegisterService {

    private final BadgeRepository badgeRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public AttendanceRegisterResponse registerAttendance(User user, AttendanceRegisterRequest registerRequest) {
        validateAttendance(user, registerRequest.getLongitude(), registerRequest.getLatitude());
        Attendance attendance = registerRequest.toEntity(user);
        attendanceRepository.save(attendance);
        user.updateAttendanceDate();
        return updateNewBadges(user);
    }

    private void validateAttendance(User user, double longitude, double latitude) {
        validateLocation(longitude, latitude);
        validateDuplicateAttendance(user);
    }

    private void validateLocation(double longitude, double latitude) {
        boolean isLocatedInKU = LocationUtils.isLocatedInKU(latitude, longitude);
        if (!isLocatedInKU) {
            throw new InvalidAttendanceLocationException();
        }
    }

    private void validateDuplicateAttendance(User user) {
        if (attendanceRepository.existsByUserAndCreatedAtAfter(user, LocalDate.now().atStartOfDay())) {
            throw new DuplicateAttendanceException();
        }
    }

    private AttendanceRegisterResponse updateNewBadges(User user) {
        Set<BadgeType> userBadgeSet = getUserBadgeSet(user);

        AttendanceRegisterResponse response = new AttendanceRegisterResponse();
        // TODO. 뱃지 생성 로직 리팩토링(클래스 분리 등)
        if (!userBadgeSet.contains(BadgeType.ATTENDANCE_1)) {
            badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_1));
            response.addBadge(BadgeType.ATTENDANCE_1);
        }
        else if (!userBadgeSet.contains(BadgeType.ATTENDANCE_3)) {
            if (isEligibleForAttendanceBadge(user, 3)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_3));
                response.addBadge(BadgeType.ATTENDANCE_3);
            }
        }
        else if (!userBadgeSet.contains(BadgeType.ATTENDANCE_7)) {
            if (isEligibleForAttendanceBadge(user, 7)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_7));
                response.addBadge(BadgeType.ATTENDANCE_7);
            }
        }
        else if (!userBadgeSet.contains(BadgeType.ATTENDANCE_30)) {
            if (isEligibleForAttendanceBadge(user, 30)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_30));
                response.addBadge(BadgeType.ATTENDANCE_30);
            }
        }
        else if (!userBadgeSet.contains(BadgeType.ATTENDANCE_100)) {
            if (isEligibleForAttendanceBadge(user, 100)) {
                badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_100));
                response.addBadge(BadgeType.ATTENDANCE_100);
            }
        }
        if (isTodayFoundationDay() && !userBadgeSet.contains(BadgeType.ATTENDANCE_FOUNDATION_DAY)) {
            badgeRepository.save(Badge.createBadge(user, BadgeType.ATTENDANCE_FOUNDATION_DAY));
            response.addBadge(BadgeType.ATTENDANCE_FOUNDATION_DAY);
        }
        return response;
    }

    private Set<BadgeType> getUserBadgeSet(User user) {
        return badgeRepository.findByUser(user).stream()
                .map(Badge::getBadgeType)
                .collect(Collectors.toSet());
    }

    private boolean isEligibleForAttendanceBadge(User user, Integer consecutiveDays) {
        Integer userConsecutiveAttendanceDays = user.getConsecutiveAttendanceDays();
        return userConsecutiveAttendanceDays.equals(consecutiveDays);
    }

    private boolean isTodayFoundationDay() {
        LocalDate today = LocalDate.now();
        return today.getMonth().getValue() == 5 && today.getDayOfMonth() == 15;
    }

}
