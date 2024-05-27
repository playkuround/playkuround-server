package com.playkuround.playkuroundserver.domain.systemcheck.api;

import com.playkuround.playkuroundserver.domain.systemcheck.application.SystemCheckService;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/system-available")
@RequiredArgsConstructor
@Tag(name = "System Check")
public class SystemCheckApi {

    private final SystemCheckService systemCheckService;

    @PostMapping
    @Operation(summary = "시스템 점검 유무 변경하기", description = "시스템 점검 유무를 변경합니다.")
    public ApiResponse<Void> changeSystemAvailable(@RequestParam("available") boolean appVersion) {
        systemCheckService.changeSystemAvailable(appVersion);
        return ApiUtils.success(null);
    }

}
