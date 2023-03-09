package com.playkuround.playkuroundserver.domain.adventure.dao;

import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.VisitedUserDto;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {

    @Query(value = "SELECT DISTINCT a.landmark.id FROM Adventure a WHERE a.user.id=:#{#user.id}")
    List<Long> findDistinctLandmarkIdByUser(@Param(value = "user") User user);

    @Query(value = "SELECT COUNT(DISTINCT a.landmark.id) FROM Adventure a WHERE a.user.id=:#{#user.id}")
    Long countDistinctLandmarkByUser(@Param(value = "user") User user);

    @Query(value =
            "SELECT " +
                    "count(a.user.id) as number, a.user.nickname as nickname, a.user.id as userId " +
                    "FROM Adventure a " +
                    "where a.landmark.id=:landmark " +
                    "GROUP BY a.user.id " +
                    "ORDER BY count(a.user.id) DESC, max(a.updatedAt) ASC "
    )
    List<VisitedUserDto> findVisitedUsersRank(@Param(value = "landmark") Long landmarkId);


    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id>=22 and a.landmark.id<=26")
    Long countAdventureForENGINEER();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id=8 or a.landmark.id=28")
    Long countAdventureForARTIST();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id=14 or a.landmark.id=15 or a.landmark.id=19")
    Long countAdventureForCEO();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id=37 or a.landmark.id=38")
    Long countAdventureForNATIONAL_PLAYER();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id>=39 and a.landmark.id<=44")
    Long countAdventureForNEIL_ARMSTRONG();

    boolean existsByUserAndLandmarkAndCreatedAtAfter(User user, Landmark landmark, LocalDateTime localDateTime);
}
