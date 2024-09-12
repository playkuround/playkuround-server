package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceSearchService {

    private final AttendanceRepository attendanceRepository;
    private final DateTimeService dateTimeService;

    @Transactional(readOnly = true)
    public List<LocalDateTime> findAttendance(User user, int agoDays) {
        LocalDateTime monthAgo = dateTimeService.getLocalDateNow()
                .minusDays(agoDays)
                .atStartOfDay();

        List<Attendance> attendances = attendanceRepository.findByUserAndAttendanceDateTimeGreaterThanEqual(user, monthAgo);
        return attendances.stream()
                .map(Attendance::getAttendanceDateTime)
                .sorted()
                .toList();
    }
}
