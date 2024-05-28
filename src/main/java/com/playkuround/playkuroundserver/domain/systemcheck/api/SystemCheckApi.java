package com.playkuround.playkuroundserver.domain.systemcheck.api;

import com.playkuround.playkuroundserver.domain.systemcheck.api.response.HealthCheckResponse;
import com.playkuround.playkuroundserver.domain.systemcheck.application.SystemCheckService;
import com.playkuround.playkuroundserver.domain.systemcheck.dto.HealthCheckDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/system-available")
@RequiredArgsConstructor
@Tag(name = "System Check")
public class SystemCheckApi {

    private final SystemCheckService systemCheckService;

    @PostMapping
    @Operation(summary = "시스템 사용가능 여부 변경하기", description = "시스템 점검 유무를 변경합니다.")
    public ApiResponse<Void> changeSystemAvailable(@RequestParam("available") boolean appVersion) {
        systemCheckService.changeSystemAvailable(appVersion);
        return ApiUtils.success(null);
    }

    @GetMapping
    @Operation(summary = "시스템 사용가능 여부 체크", description = "현재 서버의 상태를 점검합니다.")
    public ApiResponse<HealthCheckResponse> healthCheck() {
        HealthCheckDto healthCheckDto = systemCheckService.healthCheck();
        HealthCheckResponse response = new HealthCheckResponse(healthCheckDto.systemAvailable(), healthCheckDto.supportAppVersionList());
        return ApiUtils.success(response);
    }

}
