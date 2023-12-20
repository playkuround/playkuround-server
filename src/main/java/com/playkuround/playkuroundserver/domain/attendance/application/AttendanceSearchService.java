package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.common.BaseTimeEntity;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceSearchService {

    private final AttendanceRepository attendanceRepository;

    public List<LocalDateTime> findAttendanceForMonth(User user) {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        List<Attendance> attendances = attendanceRepository.findByUserAndCreatedAtAfter(user, monthAgo);
        return attendances.stream()
                .map(BaseTimeEntity::getCreatedAt)
                .sorted()
                .toList();
    }
}
