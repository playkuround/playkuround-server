package com.playkuround.playkuroundserver.domain.score.api;

import com.playkuround.playkuroundserver.domain.score.application.ScoreService;
import com.playkuround.playkuroundserver.domain.score.dto.SaveScore;
import com.playkuround.playkuroundserver.global.resolver.UserEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/scores")
public class ScoreApi {

    private final ScoreService scoreService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAdventure(@UserEmail String userEmail, @RequestBody @Valid SaveScore saveScore) {
        scoreService.saveScore(userEmail, saveScore);
    }

}
