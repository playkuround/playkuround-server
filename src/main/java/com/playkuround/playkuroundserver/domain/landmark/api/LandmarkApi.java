package com.playkuround.playkuroundserver.domain.landmark.api;

import com.playkuround.playkuroundserver.domain.landmark.application.LandmarkFindNearService;
import com.playkuround.playkuroundserver.domain.landmark.application.LandmarkScoreService;
import com.playkuround.playkuroundserver.domain.landmark.dto.response.LandmarkHighestScoreUser;
import com.playkuround.playkuroundserver.domain.landmark.dto.response.NearestLandmarkResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/landmarks")
@RequiredArgsConstructor
@Tag(name = "Landmark", description = "Landmark API")
public class LandmarkApi {

    private final LandmarkFindNearService landmarkFindNearService;
    private final LandmarkScoreService landmarkScoreService;

    @GetMapping
    @Operation(summary = "가장 가까운 랜드마크 찾기",
            description = "인식 반경 내에 있는 랜드마크 중 가장 가까운 랜드마크를 반환합니다." +
                    "인식 반경에 랜드마크가 없을 경우 아무것도 반환하지 않습니다.")
    public ApiResponse<NearestLandmarkResponse> LandmarkFindNear(@RequestParam @Latitude Double latitude,
                                                                 @RequestParam @Longitude Double longitude) {
        NearestLandmarkResponse response = landmarkFindNearService.findNearestLandmark(latitude, longitude);
        return ApiUtils.success(response);
    }

    @GetMapping("/{landmarkId}/highest")
    public ApiResponse<LandmarkHighestScoreUser> findHighestUserByLandmark(@PathVariable Long landmarkId) {
        LandmarkHighestScoreUser response = landmarkScoreService.findHighestScoreUserByLandmark(landmarkId);
        return ApiUtils.success(response);
    }
}
