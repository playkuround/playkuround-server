package com.playkuround.playkuroundserver.domain.quiz.application;

import com.playkuround.playkuroundserver.domain.quiz.dao.QuizFindDao;
import com.playkuround.playkuroundserver.domain.quiz.domain.Quiz;
import com.playkuround.playkuroundserver.domain.quiz.dto.QuizDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizFindDao quizFindDao;

    public QuizDto.Response getQuizzes(Long landmarkId) {
        Quiz quiz = quizFindDao.findByLandmarkId(landmarkId);
        return QuizDto.Response.of(quiz);
    }
}