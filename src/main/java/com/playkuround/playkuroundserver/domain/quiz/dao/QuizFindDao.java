package com.playkuround.playkuroundserver.domain.quiz.dao;

import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.quiz.domain.Quiz;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizFindDao {

    private final QuizRepository quizRepository;

    public Quiz findByLandmarkId(Long landmarkId) {
        return quizRepository.findByLandmarkId(landmarkId)
                .orElseThrow(() -> new LandmarkNotFoundException(landmarkId));
    }
}