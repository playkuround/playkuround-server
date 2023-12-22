package com.playkuround.playkuroundserver.domain.score.api;

import com.playkuround.playkuroundserver.domain.score.application.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/scores")
public class ScoreApi {

    private final ScoreService scoreService;
//
//    @GetMapping("/rankings/top100")
//    public ApiResponse<List<ScoreRankingDto>> scoreGetTop100() {
//        List<ScoreRankingDto> response = scoreService.getTop100();
//        return ApiUtils.success(response);
//    }
//
//    @GetMapping("/rankings")
//    public ApiResponse<ScoreRankingDto> scoreGetRanking(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        ScoreRankingDto response = scoreService.getMyRank(userDetails.getUser());
//        return ApiUtils.success(response);
//    }

}
