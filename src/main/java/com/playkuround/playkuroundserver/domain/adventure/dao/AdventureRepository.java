package com.playkuround.playkuroundserver.domain.adventure.dao;

import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScore;
import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {

    @Query(value =
            "SELECT new com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScore(a.user.nickname, cast(SUM(a.score) as integer)) " +
                    "FROM Adventure a " +
                    "where a.landmark.id=:landmark AND a.createdAt >= :from " +
                    "GROUP BY a.user.id " +
                    "ORDER BY SUM(a.score) DESC, a.user.nickname DESC " +
                    "LIMIT 100")
    List<NicknameAndScore> findRankTop100DescByLandmarkId(@Param(value = "landmark") Long landmarkId, @Param(value = "from") LocalDateTime from);

    @Query(value =
            "SELECT new com.playkuround.playkuroundserver.domain.score.dto.RankAndScore(cast(user_rank as integer), cast(score as integer)) FROM " +
                    "(SELECT a.user.id as user_id, (RANK() over (order by SUM(a.score) desc)) as user_rank, SUM(a.score) as score " +
                    "FROM Adventure a " +
                    "where a.landmark.id=:landmark " +
                    "GROUP BY a.user.id) " +
                    "where user_id=:#{#user.id}")
    Optional<RankAndScore> findMyRankByLandmarkId(@Param(value = "user") User user, @Param(value = "landmark") Long landmarkId);

    @Query("SELECT SUM(a.score) FROM Adventure a WHERE a.user.id=:#{#user.id} AND a.landmark.id=:#{#landmark.id}")
    long getSumScoreByUserAndLandmark(@Param(value = "user") User user, @Param(value = "landmark") Landmark landmark);

    long countByUserAndLandmark(User user, Landmark requestSaveLandmark);
}
