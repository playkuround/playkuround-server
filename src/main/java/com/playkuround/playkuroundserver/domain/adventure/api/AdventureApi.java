package com.playkuround.playkuroundserver.domain.adventure.api;

import com.playkuround.playkuroundserver.domain.adventure.application.AdventureService;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseMostLandmarkUser;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.resolver.UserEmail;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/adventures")
public class AdventureApi {
    private final AdventureService adventureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AdventureSaveDto.Response> saveAdventure(@UserEmail String userEmail, @RequestBody @Valid AdventureSaveDto.Request dto) {
        AdventureSaveDto.Response response = adventureService.saveAdventure(userEmail, dto);
        return ApiUtils.success(response);
    }

    @GetMapping
    public ApiResponse<List<ResponseFindAdventure>> findAdventureByUserEmail(@UserEmail String userEmail) {
        List<ResponseFindAdventure> adventureByUserEmail = adventureService.findAdventureByUserEmail(userEmail);
        return ApiUtils.success(adventureByUserEmail);

    }

    @GetMapping("/{landmarkId}/most")
    public ApiResponse<ResponseMostLandmarkUser> findMemberMostAdventure(@PathVariable Long landmarkId) {
        ResponseMostLandmarkUser memberMostLandmark = adventureService.findMemberMostLandmark(landmarkId);
        return ApiUtils.success(memberMostLandmark);
    }
}
