package com.playkuround.playkuroundserver.domain.attendance.api;

import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceRegisterService;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceSearchService;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceRegisterDto;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceSearchDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.resolver.UserEmail;
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
    public ApiResponse<AttendanceRegisterDto.Response> attendanceRegister(@UserEmail String userEmail, @Valid @RequestBody AttendanceRegisterDto.Request registerRequest) {
        AttendanceRegisterDto.Response response = attendanceRegisterService.registerAttendance(userEmail, registerRequest);
        return ApiUtils.success(response);
    }

    @GetMapping
    public ApiResponse<AttendanceSearchDto.Response> attendanceSearch(@UserEmail String userEmail) {
        List<LocalDateTime> attendances = attendanceSearchService.findByUserMonthLong(userEmail);
        return ApiUtils.success(AttendanceSearchDto.Response.of(attendances));
    }

}
