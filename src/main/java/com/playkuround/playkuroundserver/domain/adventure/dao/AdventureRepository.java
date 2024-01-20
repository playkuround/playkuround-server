package com.playkuround.playkuroundserver.domain.adventure.dao;

import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScore;
import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {

    @Query(value =
            "SELECT SUM(a.score) as score, a.user.nickname as nickname " +
                    "FROM Adventure a " +
                    "where a.landmark.id=:landmark " +
                    "GROUP BY a.user.id " +
                    "ORDER BY score DESC, nickname DESC " +
                    "LIMIT 100")
    List<NicknameAndScore> findRankTop100DescByLandmarkId(@Param(value = "landmark") Long landmarkId);

    @Query(value =
            "SELECT SUM(a.score) as score, RANK() over (order by score desc) as rank " +
                    "FROM Adventure a " +
                    "where a.landmark.id=:landmark " +
                    "GROUP BY a.user.id " +
                    "HAVING a.user.id=:#{#user.id}")
    Optional<RankAndScore> findMyRankByLandmarkId(@Param(value = "user") User user, @Param(value = "landmark") Long landmarkId);

    @Query("SELECT SUM(a.score) FROM Adventure a WHERE a.user.id=:#{#user.id} AND a.landmark.id=:#{#landmark.id}")
    long sumScoreByUserAndLandmark(@Param(value = "user") User user, @Param(value = "landmark") Landmark landmark);
}
