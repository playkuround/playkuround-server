package com.playkuround.playkuroundserver.domain.appversion.api;

import com.playkuround.playkuroundserver.domain.appversion.api.request.UpdateAppVersionRequest;
import com.playkuround.playkuroundserver.domain.appversion.application.AppVersionService;
import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.domain.appversion.dto.OSAndVersion;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin/app-version")
@RequiredArgsConstructor
public class AppVersionApi {

    private final AppVersionService appVersionService;

    @PostMapping
    @Operation(summary = "지원하는 앱 버전 업데이트", description = "지원하는 앱 버전 업데이트합니다.(덮어쓰기)", tags = "Admin")
    public ApiResponse<Void> updateAppVersion(@RequestBody @Valid UpdateAppVersionRequest request) {
        Set<OSAndVersion> requestSet = request.getOsAndVersions().stream()
                .map(osAndVersion -> {
                    OperationSystem operationSystem = OperationSystem.fromString(osAndVersion.getOs());
                    return new OSAndVersion(operationSystem, osAndVersion.getVersion());
                })
                .collect(Collectors.toSet());
        appVersionService.changeSupportedList(requestSet);

        return ApiUtils.success(null);
    }

}
