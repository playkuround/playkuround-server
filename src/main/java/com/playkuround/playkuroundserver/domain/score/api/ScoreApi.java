package com.playkuround.playkuroundserver.domain.score.api;

import com.playkuround.playkuroundserver.domain.score.application.ScoreService;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scores")
@Tag(name = "Score", description = "Score API")
public class ScoreApi {

    private final ScoreService scoreService;

    @GetMapping("/rankings/top100")
    @Operation(summary = "스코어 탑100 얻기", description = "토탈 점수 탑100과 내 점수, 등수를 반환합니다. 내 점수가 0점이면 등수는 0등으로 반환됩니다.")
    public ApiResponse<ScoreRankingResponse> getScoreTop100(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ScoreRankingResponse response = scoreService.getRankTop100(userDetails.getUser());
        return ApiUtils.success(response);
    }

}
