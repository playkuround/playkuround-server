package com.playkuround.playkuroundserver.domain.attendance.api;

import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceRegisterService;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceSearchService;
import com.playkuround.playkuroundserver.domain.attendance.dto.request.AttendanceRegisterRequest;
import com.playkuround.playkuroundserver.domain.attendance.dto.response.AttendanceRegisterResponse;
import com.playkuround.playkuroundserver.domain.attendance.dto.response.AttendanceSearchResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ApiResponse<AttendanceRegisterResponse> attendanceRegister(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @Valid @RequestBody AttendanceRegisterRequest registerRequest) {
        AttendanceRegisterResponse response = attendanceRegisterService.registerAttendance(userDetails.getUser(), registerRequest);
        return ApiUtils.success(response);
    }

    @GetMapping
    public ApiResponse<AttendanceSearchResponse> attendanceSearch(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<LocalDateTime> attendances = attendanceSearchService.findByUserMonthLong(userDetails.getUser());
        return ApiUtils.success(AttendanceSearchResponse.from(attendances));
    }

}
