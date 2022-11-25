package com.playkuround.playkuroundserver.domain.quiz.api;

import com.playkuround.playkuroundserver.domain.quiz.application.QuizService;
import com.playkuround.playkuroundserver.domain.quiz.dto.QuizDto;
import com.playkuround.playkuroundserver.global.common.response.ApiResponse;
import com.playkuround.playkuroundserver.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizApi {

    private final QuizService quizService;

    @GetMapping("{landmarkId}")
    public ApiResponse<QuizDto.Response> getQuizzes(@PathVariable(name="landmarkId") Long landmarkId) {

        QuizDto.Response quizResponse = quizService.getQuizzes(landmarkId);

        return ApiUtils.success(quizResponse);
    }
}