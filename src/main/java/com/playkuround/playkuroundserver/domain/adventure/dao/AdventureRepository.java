package com.playkuround.playkuroundserver.domain.adventure.dao;

import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.MyScore;
import com.playkuround.playkuroundserver.domain.adventure.dto.UserScore;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {

    @Query(value = "SELECT DISTINCT a.landmark.id FROM Adventure a WHERE a.user.id=:#{#user.id}")
    List<Long> findDistinctLandmarkIdByUser(@Param(value = "user") User user);

    @Query(value = "SELECT COUNT(DISTINCT a.landmark.id) FROM Adventure a WHERE a.user.id=:#{#user.id}")
    Long countDistinctLandmarkByUser(@Param(value = "user") User user);

    @Query(value =
            "SELECT SUM(a.score) as score, a.user.nickname as nickname, a.user.id as userId " +
                    "FROM Adventure a " +
                    "where a.landmark.id=:landmark " +
                    "GROUP BY a.user.id " +
                    "ORDER BY SUM(a.score) DESC")
    List<UserScore> findUserScoreRankDescByLandmarkId(@Param(value = "landmark") Long landmarkId);

    @Query(value =
            "SELECT SUM(a.score) as score, RANK() over (order by score desc) as rank " +
                    "FROM Adventure a " +
                    "where a.landmark.id=:landmark " +
                    "GROUP BY a.user.id " +
                    "HAVING a.user.id=:#{#user.id}")
    Optional<MyScore> findMyRankByLandmarkId(@Param(value = "user") User user, @Param(value = "landmark") Long landmarkId);

    @Query("SELECT SUM(a.score) FROM Adventure a WHERE a.user.id=:#{#user.id} AND a.landmark.id=:#{#landmark.id}")
    long sumScoreByUserAndLandmark(@Param(value = "user") User user, @Param(value = "landmark") Landmark landmark);
}
