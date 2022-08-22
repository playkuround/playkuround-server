package com.playkuround.playkuroundserver.domain.attendance.dao;

import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceFindDao {

    private final AttendanceRepository attendanceRepository;

    public List<Attendance> findByUserMonthLong(User user) {
        return attendanceRepository.findByUserAndCreateAtAfter(user, LocalDateTime.now().minusMonths(1));
    }

}
