package com.playkuround.playkuroundserver.domain.badge.api;

import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.dto.BadgeFindDto;
import com.playkuround.playkuroundserver.domain.badge.dto.BadgeSaveDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeApi {

    private final BadgeService badgeService;

    @GetMapping
    public ApiResponse<?> findBadge(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<BadgeFindDto> badges = badgeService.findBadgeByEmail(userDetails.getUser());
        return ApiUtils.success(badges);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> saveBadge(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @RequestBody @Valid BadgeSaveDto badgeSaveDto) {
        badgeService.registerBadge(userDetails.getUser(), badgeSaveDto.getBadgeType());
        return ApiUtils.success(null);
    }
}
