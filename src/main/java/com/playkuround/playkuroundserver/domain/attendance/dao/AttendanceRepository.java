package com.playkuround.playkuroundserver.domain.attendance.dao;

import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByUserAndAttendanceDateTimeGreaterThanEqual(User user, LocalDateTime localDateTime);

    boolean existsByUserAndAttendanceDateTimeGreaterThanEqual(User user, LocalDateTime localDateTime);

    void deleteByUser(User user);
}
