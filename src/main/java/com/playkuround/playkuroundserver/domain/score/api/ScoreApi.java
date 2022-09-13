package com.playkuround.playkuroundserver.domain.score.api;

import com.playkuround.playkuroundserver.domain.score.application.ScoreService;
import com.playkuround.playkuroundserver.domain.score.dto.ScoreRegisterDto;
import com.playkuround.playkuroundserver.domain.score.dto.ScoreRankingDto;
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
@RequestMapping("/api/scores")
public class ScoreApi {

    private final ScoreService scoreService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> saveAdventure(@UserEmail String userEmail, @RequestBody @Valid ScoreRegisterDto saveScore) {
        scoreService.saveScore(userEmail, saveScore);
        return ApiUtils.success(null);
    }

    @GetMapping("/rankings/top100")
    public ApiResponse<List<ScoreRankingDto>> scoreGetTop100() {
        return ApiUtils.success(scoreService.getTop100());
    }

    @GetMapping("/rankings")
    public ApiResponse<ScoreRankingDto> scoreGetRanking(@UserEmail String userEmail) {
        return ApiUtils.success(scoreService.getRanking(userEmail));
    }

}
