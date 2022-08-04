package com.playkuround.playkuroundserver.domain.landmark.dao;

import com.playkuround.playkuroundserver.domain.landmark.domain.Adventure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {
}
