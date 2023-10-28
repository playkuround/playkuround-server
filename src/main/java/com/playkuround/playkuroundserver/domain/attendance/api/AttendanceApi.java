package com.playkuround.playkuroundserver.domain.attendance.api;

import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceRegisterService;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceSearchService;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceRegisterDto;
import com.playkuround.playkuroundserver.domain.attendance.dto.AttendanceSearchDto;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendances")
public class AttendanceApi {

    private final AttendanceSearchService attendanceSearchService;
    private final AttendanceRegisterService attendanceRegisterService;

    @PostMapping
    public ApiResponse<Void> attendanceRegister(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @Valid @RequestBody AttendanceRegisterDto.Request registerRequest) {
        attendanceRegisterService.registerAttendance(userDetails.getUser(), registerRequest);
        return ApiUtils.success(null);
    }

    @GetMapping
    public ApiResponse<AttendanceSearchDto.Response> attendanceSearch(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<LocalDateTime> attendances = attendanceSearchService.findByUserMonthLong(userDetails.getUser());
        return ApiUtils.success(AttendanceSearchDto.Response.of(attendances));
    }

}
