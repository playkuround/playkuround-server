package com.playkuround.playkuroundserver.domain.adventure.api;

import com.playkuround.playkuroundserver.domain.adventure.api.request.AdventureSaveRequest;
import com.playkuround.playkuroundserver.domain.adventure.api.response.AdventureSaveResponse;
import com.playkuround.playkuroundserver.domain.adventure.application.AdventureService;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.badge.dto.NewlyRegisteredBadge;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.score.exception.ScoreTypeNotMatchException;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import com.playkuround.playkuroundserver.global.util.Location;
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
@Tag(name = "Adventure", description = "탐험하기 API")
public class AdventureApi {

    private final AdventureService adventureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "탐험하기", description = "탐험 점수를 저장합니다. 새롭게 얻은 배지가 있을 시 반환됩니다. " +
            "새로 추가된 배지는 DB에 자동 반영됩니다. scoreType은 별도 문서 참고")
    public ApiResponse<AdventureSaveResponse> saveAdventure(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestBody @Valid AdventureSaveRequest request) {
        ScoreType scoreType = ScoreType.fromString(request.getScoreType())
                .orElseThrow(ScoreTypeNotMatchException::new);
        Location location = new Location(request.getLatitude(), request.getLongitude());
        AdventureSaveDto adventureSaveDto
                = new AdventureSaveDto(userDetails.getUser(), request.getLandmarkId(), location, request.getScore(), scoreType);

        NewlyRegisteredBadge newlyRegisteredBadge = adventureService.saveAdventure(adventureSaveDto);
        return ApiUtils.success(AdventureSaveResponse.from(newlyRegisteredBadge));
    }

}
