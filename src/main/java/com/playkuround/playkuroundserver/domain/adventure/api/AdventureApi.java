package com.playkuround.playkuroundserver.domain.adventure.api;

import com.playkuround.playkuroundserver.domain.adventure.application.AdventureService;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.adventure.dto.ResponseFindAdventure;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/adventures")
public class AdventureApi {
    private final AdventureService adventureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<?> saveAdventure(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestBody @Valid AdventureSaveDto.Request request) {
        adventureService.saveAdventure(userDetails.getUser(), request);
        return ApiUtils.success(null);
    }

    @GetMapping
    public ApiResponse<ResponseFindAdventure> findAdventureByUserEmail(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResponseFindAdventure adventureByUserEmail = adventureService.findAdventureByUserEmail(userDetails.getUser());
        return ApiUtils.success(adventureByUserEmail);

    }
}
