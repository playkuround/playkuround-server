package com.playkuround.playkuroundserver.domain.adventure.api;

import com.playkuround.playkuroundserver.domain.adventure.application.AdventureService;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseMostVisitedUser;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/adventures")
public class AdventureApi {
    private final AdventureService adventureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> saveAdventure(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestBody @Valid AdventureSaveDto.Request request) {
        adventureService.saveAdventure(userDetails, request);
        return ApiUtils.success(null);
    }

    @GetMapping
    public ApiResponse<ResponseFindAdventure> findAdventureByUserEmail(@AuthenticationPrincipal UserDetails userDetails) {
        ResponseFindAdventure adventureByUserEmail = adventureService.findAdventureByUserEmail(userDetails);
        return ApiUtils.success(adventureByUserEmail);

    }

    @GetMapping("/{landmarkId}/most")
    public ApiResponse<ResponseMostVisitedUser> findMemberMostAdventure(@AuthenticationPrincipal UserDetails userDetails,
                                                                        @PathVariable Long landmarkId) {
        ResponseMostVisitedUser memberMostLandmark = adventureService.findMemberMostLandmark(userDetails, landmarkId);
        return ApiUtils.success(memberMostLandmark);
    }
}
