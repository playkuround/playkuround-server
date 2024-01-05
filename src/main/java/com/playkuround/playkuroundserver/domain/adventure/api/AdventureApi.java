package com.playkuround.playkuroundserver.domain.adventure.api;

import com.playkuround.playkuroundserver.domain.adventure.application.AdventureService;
import com.playkuround.playkuroundserver.domain.adventure.dto.request.AdventureSaveRequest;
import com.playkuround.playkuroundserver.domain.adventure.dto.response.AdventureSaveResponse;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adventures")
@Tag(name = "Adventure", description = "Adventure API")
public class AdventureApi {

    private final AdventureService adventureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "탐험하기", description = "탐험 점수를 저장합니다. 새롭게 얻은 뱃지가 있을 시 반환됩니다. " +
            "뱃지는 DB에 자동 반영됩니다. scoreType은 별도 문서 참고")
    public ApiResponse<AdventureSaveResponse> saveAdventure(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestBody @Valid AdventureSaveRequest request) {
        AdventureSaveResponse response = adventureService.saveAdventure(userDetails.getUser(), request);
        return ApiUtils.success(response);
    }

}
