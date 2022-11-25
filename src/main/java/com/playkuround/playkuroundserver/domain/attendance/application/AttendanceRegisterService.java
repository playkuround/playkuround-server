package com.playkuround.playkuroundserver.domain.attendance.application;

import com.playkuround.playkuroundserver.domain.attendance.dao.AttendanceRepository;
import com.playkuround.playkuroundserver.domain.attendance.domain.Attendance;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceRegisterDto;
import com.playkuround.playkuroundserver.domain.attendance.exception.InvalidAttendanceLocationException;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.LocationUtils;
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
        double latitude = registerRequest.getLatitude();
        double longitude = registerRequest.getLongitude();

        boolean locatedInKU = LocationUtils.isLocatedInKU(latitude, longitude);
        if (!locatedInKU) {
            throw new InvalidAttendanceLocationException();
        }

        User user = userFindDao.findByEmail(userEmail);
        Attendance attendance = Attendance.createAttendance(latitude, longitude, user);
        attendanceRepository.save(attendance);
    }

}
