package com.playkuround.playkuroundserver.domain.badge.api;

import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.request.ManualBadgeSaveRequest;
import com.playkuround.playkuroundserver.domain.badge.dto.response.BadgeFindResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@Tag(name = "Badge", description = "Badge API")
public class BadgeApi {

    private final BadgeService badgeService;

    @GetMapping
    @Operation(summary = "뱃지조회", description = "사용자가 획득한 뱃지를 조회합니다.")
    public ApiResponse<List<BadgeFindResponse>> findBadge(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<BadgeFindResponse> response = badgeService.findBadgeByEmail(userDetails.getUser());
        return ApiUtils.success(response);
    }

    @PostMapping("/dream-of-duck")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "오리의꿈 뱃지 획득", description = "오리의 꿈 뱃지를 획득합니다. 이미 획득한 뱃지였다면 false를 반환합니다.")
    public ApiResponse<Boolean> saveTheDreamOfDuckBadge(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean response = badgeService.saveTheDreamOfDuckBadge(userDetails.getUser());
        return ApiUtils.success(response);
    }

    @PostMapping("/manual")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "수동 뱃지 등록(관리자권한)", description = "수동으로 뱃지를 등록합니다. 이미 획득한 뱃지였다면 false를 반환합니다. \n" +
            "설정에 따라 개인 메시지로 등록할 수 있습니다. 관리자 권한이 없는 경우 403 에러가 발생합니다.")
    public ApiResponse<Boolean> saveManualBadge(@RequestBody @Valid ManualBadgeSaveRequest request) {
        BadgeType badgeType = BadgeType.valueOf(request.getBadge());
        boolean response = badgeService.saveManualBadge(request.getUserEmail(), badgeType, request.isRegisterMessage());
        return ApiUtils.success(response);
    }
}
