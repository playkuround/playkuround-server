package com.playkuround.playkuroundserver.domain.landmark.dao;

import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {
}
