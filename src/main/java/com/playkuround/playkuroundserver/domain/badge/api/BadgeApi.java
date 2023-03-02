package com.playkuround.playkuroundserver.domain.badge.api;

import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.dto.BadgeFindDto;
import com.playkuround.playkuroundserver.domain.badge.dto.BadgeSaveDto;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.resolver.UserEntity;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeApi {

    private final BadgeService badgeService;

    @GetMapping
    public ApiResponse<?> findBadge(@UserEntity User user) {
        List<BadgeFindDto> badges = badgeService.findBadgeByEmail(user);
        return ApiUtils.success(badges);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> saveBadge(@UserEntity User user,
                                    @RequestBody @Valid BadgeSaveDto badgeSaveDto) {
        badgeService.registerBadge(user, badgeSaveDto.getBadgeType());
        return ApiUtils.success(null);
    }
}
