package com.playkuround.playkuroundserver.domain.landmark.dao;

import com.playkuround.playkuroundserver.domain.landmark.domain.Adventure;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {
    List<Adventure> findAllByUser(User user);
}
