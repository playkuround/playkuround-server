package com.playkuround.playkuroundserver.domain.landmark.api;

import com.playkuround.playkuroundserver.domain.landmark.api.response.LandmarkHighestScoreUserResponse;
import com.playkuround.playkuroundserver.domain.landmark.api.response.NearestLandmarkResponse;
import com.playkuround.playkuroundserver.domain.landmark.application.LandmarkFindNearService;
import com.playkuround.playkuroundserver.domain.landmark.application.LandmarkScoreService;
import com.playkuround.playkuroundserver.domain.landmark.dto.LandmarkHighestScoreUser;
import com.playkuround.playkuroundserver.domain.landmark.dto.NearestLandmark;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import com.playkuround.playkuroundserver.global.util.Location;
import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/landmarks")
@RequiredArgsConstructor
@Tag(name = "Landmark", description = "랜드마크 API")
public class LandmarkApi {

    private final LandmarkScoreService landmarkScoreService;
    private final LandmarkFindNearService landmarkFindNearService;

    @GetMapping
    @Operation(summary = "가장 가까운 랜드마크 찾기",
            description = "인식 반경 내에 있는 랜드마크 중 가장 가까운 랜드마크를 반환합니다. " +
                    "인식 반경에 랜드마크가 없을 경우 아무것도 반환하지 않습니다.")
    public ApiResponse<NearestLandmarkResponse> findNearestLandmark(@RequestParam @Latitude Double latitude,
                                                                    @RequestParam @Longitude Double longitude) {
        Location location = new Location(latitude, longitude);
        NearestLandmark nearestLandmark = landmarkFindNearService.findNearestLandmark(location);
        return ApiUtils.success(NearestLandmarkResponse.from(nearestLandmark));
    }

    @GetMapping("{landmarkId}/highest")
    @Operation(summary = "해당 랜드마크의 최고점 사용자 찾기",
            description = "해당 랜드마크에서 가장 높은 점수를 획득한 사용자를 반환합니다. " +
                    "방문한 유저가 한명도 없으면 아무것도 반환하지 않습니다. 점수가 같은 유저가 있다면 먼저 해당 점수를 달성한 유저를 반환합니다.")
    public ApiResponse<LandmarkHighestScoreUserResponse> findHighestUserByLandmark(@PathVariable Long landmarkId) {
        Optional<LandmarkHighestScoreUser> highestScoreUser = landmarkScoreService.findHighestScoreUserByLandmark(landmarkId);
        return highestScoreUser
                .map(user -> ApiUtils.success(LandmarkHighestScoreUserResponse.from(user)))
                .orElseGet(() -> ApiUtils.success(LandmarkHighestScoreUserResponse.createEmptyResponse()));
    }
}
