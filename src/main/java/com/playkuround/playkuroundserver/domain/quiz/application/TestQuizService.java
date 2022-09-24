package com.playkuround.playkuroundserver.domain.quiz.application;

import com.playkuround.playkuroundserver.domain.quiz.dao.QuizRepository;
import com.playkuround.playkuroundserver.domain.quiz.domain.Quiz;
import com.playkuround.playkuroundserver.domain.quiz.dto.QuizDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestQuizService {

    private final QuizRepository quizRepository;

    public void SaveQuizzes(Long landmarkId) {
        Quiz quiz = Quiz.builder()
                .landmarkId(landmarkId)
                .question("question")
                .example1("ex1")
                .example2("ex2")
                .example3("ex3")
                .example4("ex4")
                .answer(3)
                .build();

        quizRepository.save(quiz);
    }
}
