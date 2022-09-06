package com.playkuround.playkuroundserver.domain.score.dao;

import com.playkuround.playkuroundserver.domain.score.domain.Score;
import com.playkuround.playkuroundserver.domain.score.dto.ScoreRankingDto;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByUser(User user);

    @Query(value = "select ranking, nickname, points " +
            "from (select rank() over (order by points desc) as ranking, (select u.nickname from user as u where id = user_id) as nickname, points " +
            "        from (select adventure + attendance + extra_adventure as points, user_id " +
            "            from (select sum(IF(score_type = 'ADVENTURE', 5, 0)) as adventure, " +
            "                sum(IF(score_type = 'ATTENDANCE', 1, 0)) as attendance, " +
            "                sum(IF(score_type = 'EXTRA_ADVENTURE', 1, 0)) as extra_adventure, " +
            "                user_id " +
            "                from score " +
            "                group by user_id) as q1) as q2) as q3 " +
            "where ranking <= 100", nativeQuery = true)
    List<ScoreRankingDto> findTop100();

    @Query(value = "select ranking, nickname, points " +
            "from (select rank() over (order by points desc) as ranking, user_id, (select u.nickname from user as u where id = user_id) as nickname, points " +
            "        from (select adventure + attendance + extra_adventure as points, user_id " +
            "            from (select sum(IF(score_type = 'ADVENTURE', 5, 0)) as adventure, " +
            "                sum(IF(score_type = 'ATTENDANCE', 1, 0)) as attendance, " +
            "                sum(IF(score_type = 'EXTRA_ADVENTURE', 1, 0)) as extra_adventure, " +
            "                user_id " +
            "                from score " +
            "                group by user_id) as q1) as q2) as q3 " +
            "where user_id = :user_id", nativeQuery = true)
    Optional<ScoreRankingDto> findRankingByUser(@Param("user_id") Long userId);

}
