package com.playkuround.playkuroundserver.domain.score.dao;

import com.playkuround.playkuroundserver.domain.score.domain.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}
