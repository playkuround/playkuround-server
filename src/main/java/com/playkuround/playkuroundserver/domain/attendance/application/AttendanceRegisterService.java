package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceRegisterDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceRegisterService {

    private final UserFindDao userFindDao;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public void registerAttendance(String userEmail, AttendanceRegisterDto.Request registerRequest) {
        // TODO 학교 내에 있는지 validation 추가하기

        User user = userFindDao.findByEmail(userEmail);
        Attendance attendance = Attendance.createAttendance(user);
        attendanceRepository.save(attendance);
    }

}
