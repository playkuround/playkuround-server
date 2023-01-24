package com.playkuround.playkuroundserver.domain.adventure.dao;

import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.VisitedUserDto;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {

    @Query(value = "SELECT DISTINCT a.landmark.id FROM Adventure a WHERE a.user.id=:#{#user.id}")
    List<Long> findDistinctLandmarkIdByUser(@Param(value = "user") User user);

    @Query(value =
            "SELECT " +
                    "count(a.\"user_id\") as number, u.\"nickname\" as nickname, a.\"user_id\" as userId " +
                    "FROM \"adventure\" a " +
                    "cross join \"user\" u " +
                    "where a.\"user_id\"=u.\"id\" and a.\"landmark_id\"=:landmark " +
                    "GROUP BY a.\"user_id\" " +
                    "ORDER BY count(a.\"user_id\") DESC, max(a.\"updated_at\") ASC " +
                    "limit 5",
            nativeQuery = true
    )
    List<VisitedUserDto> findTop5VisitedUser(@Param(value = "landmark") Long landmarkId);


    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id>=22 and a.landmark.id<=26")
    Long countAdventureForENGINEER();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id=8 or a.landmark.id=28")
    Long countAdventureForARTIST();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id=15")
    Long countAdventureForCEO();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id=37 or a.landmark.id=38")
    Long countAdventureForNATIONAL_PLAYER();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id>=39 and a.landmark.id<=44")
    Long countAdventureForNEIL_ARMSTRONG();

    Long countByUser(User user);

    Integer countAdventureByUserAndLandmark(User user, Landmark landmark);
}
