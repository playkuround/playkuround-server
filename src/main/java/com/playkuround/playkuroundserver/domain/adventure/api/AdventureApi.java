package com.playkuround.playkuroundserver.domain.adventure.api;

import com.playkuround.playkuroundserver.domain.adventure.application.AdventureService;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseMostVisitedUser;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.resolver.UserEntity;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/adventures")
public class AdventureApi {
    private final AdventureService adventureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AdventureSaveDto.Response> saveAdventure(@UserEntity User user, @RequestBody @Valid AdventureSaveDto.Request dto) {
        AdventureSaveDto.Response response = adventureService.saveAdventure(user, dto);
        return ApiUtils.success(response);
    }

    @GetMapping
    public ApiResponse<ResponseFindAdventure> findAdventureByUserEmail(@UserEntity User user) {
        ResponseFindAdventure adventureByUserEmail = adventureService.findAdventureByUserEmail(user);
        return ApiUtils.success(adventureByUserEmail);

    }

    @GetMapping("/{landmarkId}/most")
    public ApiResponse<ResponseMostVisitedUser> findMemberMostAdventure(@UserEntity User user,
                                                                        @PathVariable Long landmarkId) {
        ResponseMostVisitedUser memberMostLandmark = adventureService.findMemberMostLandmark(user, landmarkId);
        return ApiUtils.success(memberMostLandmark);
    }
}
