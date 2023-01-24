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

    private final UserFindDao userFindDao;
    private final AttendanceRepository attendanceRepository;
    private final BadgeRepository badgeRepository;

    @Transactional
    public AttendanceRegisterDto.Response registerAttendance(String userEmail, AttendanceRegisterDto.Request registerRequest) {
        double latitude = registerRequest.getLatitude();
        double longitude = registerRequest.getLongitude();

        boolean locatedInKU = LocationUtils.isLocatedInKU(latitude, longitude);
        if (!locatedInKU) {
            throw new InvalidAttendanceLocationException();
        }

        User user = userFindDao.findByEmail(userEmail);
        if (attendanceRepository.existsByUserAndCreatedAtAfter(user, LocalDate.now().atStartOfDay())) {
            throw new DuplicateAttendanceException();
        }
        Attendance attendance = Attendance.createAttendance(latitude, longitude, user);
        attendanceRepository.save(attendance);
        return findNewBadges(user);
    }

    private AttendanceRegisterDto.Response findNewBadges(User user) {
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

        AttendanceRegisterDto.Response ret = new AttendanceRegisterDto.Response();
        if (!hasAttendance_1) ret.addBadge(BadgeType.ATTENDANCE_1);
        else if (!hasAttendance_3) {
            LocalDateTime afterDate =
                    LocalDateTime.of(LocalDate.now().minusDays(3), LocalTime.of(0, 0, 0));
            if (attendanceRepository.countByUserAndCreatedAtAfter(user, afterDate) == 3L)
                ret.addBadge(BadgeType.ATTENDANCE_3);
        }
        else if (!hasAttendance_7) {
            LocalDateTime afterDate =
                    LocalDateTime.of(LocalDate.now().minusDays(7), LocalTime.of(0, 0, 0));
            if (attendanceRepository.countByUserAndCreatedAtAfter(user, afterDate) == 7L)
                ret.addBadge(BadgeType.ATTENDANCE_7);
        }
        else if (!hasAttendance_30) {
            LocalDateTime afterDate =
                    LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.of(0, 0, 0));
            if (attendanceRepository.countByUserAndCreatedAtAfter(user, afterDate) == 30L)
                ret.addBadge(BadgeType.ATTENDANCE_30);
        }
        else if (!hasAttendance_100) {
            LocalDateTime afterDate =
                    LocalDateTime.of(LocalDate.now().minusDays(100), LocalTime.of(0, 0, 0));
            if (attendanceRepository.countByUserAndCreatedAtAfter(user, afterDate) == 100L)
                ret.addBadge(BadgeType.ATTENDANCE_100);
        }

        if (isTodayFoundationDay() && !hasAttendance_Foundation_Day) {
            ret.addBadge(BadgeType.ATTENDANCE_FOUNDATION_DAY);
        }
        return ret;
    }

    private boolean isTodayFoundationDay() {
        return LocalDate.now().getMonth().getValue() == 5 && LocalDate.now().getDayOfMonth() == 15;
    }

}
