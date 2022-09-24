package com.playkuround.playkuroundserver.domain.quiz.dao;

import com.playkuround.playkuroundserver.domain.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByLandmarkId(Long landmarkId);
}
