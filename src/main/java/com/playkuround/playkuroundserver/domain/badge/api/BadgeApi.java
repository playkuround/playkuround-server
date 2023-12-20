package com.playkuround.playkuroundserver.domain.badge.api;

import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.dto.request.BadgeFindRequest;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
@Tag(name = "Badge", description = "Badge API")
public class BadgeApi {

    private final BadgeService badgeService;

    @GetMapping
    @Operation(summary = "뱃지조회", description = "사용자가 획득한 뱃지를 조회합니다.")
    public ApiResponse<List<BadgeFindRequest>> findBadge(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<BadgeFindRequest> badges = badgeService.findBadgeByEmail(userDetails.getUser());
        return ApiUtils.success(badges);
    }
}
