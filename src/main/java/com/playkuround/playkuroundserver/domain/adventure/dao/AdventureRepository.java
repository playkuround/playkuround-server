package com.playkuround.playkuroundserver.domain.adventure.dao;

import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {
    List<Adventure> findAllByUser(User user);

    List<Adventure> findAllByLandmark(Landmark landmarkId);

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id>=22 and a.landmark.id<=26")
    Long countAdventureForENGINEER();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id=8 or a.landmark.id=28")
    Long countAdventureForARTIST();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id=15")
    Long countAdventureForCEO();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id=37 or a.landmark.id=38")
    Long countAdventureForNATIONAL_PLAYER();

    @Query("SELECT COUNT(*) FROM Adventure a where a.landmark.id>=39 or a.landmark.id<=44")
    Long countAdventureForNEIL_ARMSTRONG();

    Long countByUser(User user);
}
