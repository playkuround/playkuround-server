package com.playkuround.playkuroundserver.domain.attendance.api;

import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceRegisterService;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceSearchService;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceRegisterDto;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceSearchDto;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.resolver.UserEntity;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendances")
public class AttendanceApi {

    private final AttendanceSearchService attendanceSearchService;
    private final AttendanceRegisterService attendanceRegisterService;

    @PostMapping
    public ApiResponse<?> attendanceRegister(@UserEntity User user, @Valid @RequestBody AttendanceRegisterDto.Request registerRequest) {
        attendanceRegisterService.registerAttendance(user, registerRequest);
        return ApiUtils.success(null);
    }

    @GetMapping
    public ApiResponse<AttendanceSearchDto.Response> attendanceSearch(@UserEntity User user) {
        List<LocalDateTime> attendances = attendanceSearchService.findByUserMonthLong(user);
        return ApiUtils.success(AttendanceSearchDto.Response.of(attendances));
    }

}
