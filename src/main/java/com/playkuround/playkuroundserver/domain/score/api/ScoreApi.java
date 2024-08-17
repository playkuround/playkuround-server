package com.playkuround.playkuroundserver.domain.score.api;

import com.playkuround.playkuroundserver.domain.score.api.response.LandmarkScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.score.api.response.TotalScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.score.application.LandmarkRankService;
import com.playkuround.playkuroundserver.domain.score.application.TotalScoreService;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scores")
@Tag(name = "Score", description = "Score API")
public class ScoreApi {

    private final TotalScoreService totalScoreService;
    private final LandmarkRankService landmarkRankService;

    @GetMapping("/rank")
    @Operation(summary = "종합 점수 탑100 얻기",
            description = "토탈 점수 탑100과 내 점수, 등수를 반환합니다. 내 점수가 0점이면 등수는 0등으로 반환됩니다.")
    public ApiResponse<TotalScoreRankingResponse> getScoreTop100(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        TotalScoreRankingResponse response = totalScoreService.getRankTop100(userDetails.getUser());
        return ApiUtils.success(response);
    }

    @GetMapping("/rank/{landmarkId}")
    @Operation(summary = "해당 랜드마크의 점수 탑100 얻기",
            description = "해당 랜드마크 점수 탑100과 내 점수, 등수를 반환합니다. 내 점수가 0점이면 등수는 0등으로 반환됩니다.")
    public ApiResponse<LandmarkScoreRankingResponse> getScoreTop100ByLandmark(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                              @PathVariable Long landmarkId) {
        LandmarkScoreRankingResponse response = landmarkRankService.getRankTop100ByLandmark(userDetails.getUser(), landmarkId);
        return ApiUtils.success(response);
    }

}
