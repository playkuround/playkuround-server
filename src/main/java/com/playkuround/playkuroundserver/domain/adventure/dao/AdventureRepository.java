package com.playkuround.playkuroundserver.domain.adventure.dao;

import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.UserAndScore;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScoreAndBadgeType;
import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {

    @Query("SELECT new com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScoreAndBadgeType(a.user.nickname, cast(SUM(a.score) as integer), a.user.profileBadge) " +
            "FROM Adventure a " +
            "where a.landmark.id=:landmark AND a.createdAt >= :from " +
            "GROUP BY a.user.id " +
            "ORDER BY SUM(a.score) DESC, a.user.nickname DESC " +
            "LIMIT 100")
    List<NicknameAndScoreAndBadgeType> findRankTop100DescByLandmarkId(@Param(value = "landmark") Long landmarkId, @Param(value = "from") LocalDateTime from);

    @Query("SELECT new com.playkuround.playkuroundserver.domain.score.dto.RankAndScore(cast(user_rank as integer), cast(score as integer)) FROM " +
            "(SELECT a.user.id as user_id, (RANK() over (order by SUM(a.score) desc)) as user_rank, SUM(a.score) as score " +
            "FROM Adventure a " +
            "where a.landmark.id=:landmark AND a.createdAt >= :from " +
            "GROUP BY a.user.id) " +
            "where user_id=:#{#user.id}")
    Optional<RankAndScore> findMyRankByLandmarkId(@Param(value = "user") User user,
                                                  @Param(value = "landmark") Long landmarkId,
                                                  @Param(value = "from") LocalDateTime from);

    @Query("""
            SELECT SUM(a.score)
            FROM Adventure a
            WHERE a.user.id=:#{#user.id} AND a.landmark.id=:#{#landmark.id} AND a.createdAt >= :from
            """)
    long sumScoreByUserAndLandmarkAfter(@Param(value = "user") User user,
                                        @Param(value = "landmark") Landmark landmark,
                                        @Param(value = "from") LocalDateTime from);

    long countByUserAndLandmark(User user, Landmark landmark);

    @Query("SELECT new com.playkuround.playkuroundserver.domain.adventure.dto.UserAndScore(a.user, cast(SUM(a.score) as long)) " +
            "FROM Adventure a " +
            "where a.landmark.id=:landmark AND a.createdAt >= :from " +
            "GROUP BY a.user.id " +
            "ORDER BY SUM(a.score) DESC, a.user.nickname DESC " +
            "LIMIT :limit")
    List<UserAndScore> findRankDescBy(@Param(value = "landmark") Long landmarkId,
                                      @Param(value = "from") LocalDateTime from,
                                      @Param(value = "limit") int limit);

    void deleteByUser(User user);
}
