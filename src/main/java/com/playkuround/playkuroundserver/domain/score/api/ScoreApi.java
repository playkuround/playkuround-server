package com.playkuround.playkuroundserver.domain.score.api;

import com.playkuround.playkuroundserver.domain.score.application.ScoreService;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.security.UserDetailsImpl;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scores")
public class ScoreApi {

    private final ScoreService scoreService;

    @GetMapping("/rankings/top100")
    public ApiResponse<ScoreRankingResponse> getScoreTop100(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ScoreRankingResponse response = scoreService.getRankTop100(userDetails.getUser());
        return ApiUtils.success(response);
    }

}
