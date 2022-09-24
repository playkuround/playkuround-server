package com.playkuround.playkuroundserver.domain.quiz.api;

import com.playkuround.playkuroundserver.domain.quiz.application.QuizService;
import com.playkuround.playkuroundserver.domain.quiz.application.TestQuizService;
import com.playkuround.playkuroundserver.domain.quiz.dto.QuizDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes/test")
@RequiredArgsConstructor
public class TestQuizApi {

    private final TestQuizService testQuizService;

    @PostMapping("/{landmarkId}")
    public void saveQuizzes(@PathVariable(name="landmarkId") Long landmarkId) {
        testQuizService.SaveQuizzes(landmarkId);
    }
}