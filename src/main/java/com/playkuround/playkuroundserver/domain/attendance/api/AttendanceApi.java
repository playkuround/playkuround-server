package com.playkuround.playkuroundserver.domain.attendance.api;

import com.playkuround.playkuroundserver.domain.attendance.api.request.AttendanceRegisterRequest;
import com.playkuround.playkuroundserver.domain.attendance.api.response.AttendanceRegisterResponse;
import com.playkuround.playkuroundserver.domain.attendance.api.response.AttendanceSearchResponse;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceRegisterService;
import com.playkuround.playkuroundserver.domain.attendance.application.AttendanceSearchService;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import com.playkuround.playkuroundserver.global.util.Location;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendances")
@Tag(name = "Attendance", description = "출석 관련 API")
public class AttendanceApi {

    private final AttendanceSearchService attendanceSearchService;
    private final AttendanceRegisterService attendanceRegisterService;

    @PostMapping
    @Operation(summary = "출석하기", description = "오늘 출석을 저장합니다. 출석은 하루에 한번만 가능하며, " +
            "새롭게 얻은 뱃지가 있을 시 반환됩니다. 뱃지는 DB에 자동 반영됩니다.")
    public ApiResponse<AttendanceRegisterResponse> saveAttendance(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                  @Valid @RequestBody AttendanceRegisterRequest registerRequest) {
        Location location = new Location(registerRequest.getLatitude(), registerRequest.getLongitude());
        NewlyRegisteredBadge newlyRegisteredBadge = attendanceRegisterService.registerAttendance(userDetails.getUser(), location);
        return ApiUtils.success(AttendanceRegisterResponse.from(newlyRegisteredBadge));
    }

    @GetMapping
    @Operation(summary = "출석 조회하기", description = "30일 간의 출석 기록을 반환합니다. 가장 최신 기록이 배열의 마지막에 위치합니다.")
    public ApiResponse<AttendanceSearchResponse> searchAttendanceFor30Days(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<LocalDateTime> attendanceDateTime = attendanceSearchService.findAttendance(userDetails.getUser(), 30);
        return ApiUtils.success(AttendanceSearchResponse.from(attendanceDateTime));
    }

}
