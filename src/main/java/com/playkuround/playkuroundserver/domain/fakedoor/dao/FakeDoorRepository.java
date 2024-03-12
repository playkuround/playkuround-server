package com.playkuround.playkuroundserver.domain.fakedoor.dao;

import com.playkuround.playkuroundserver.domain.fakedoor.domain.FakeDoor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FakeDoorRepository extends JpaRepository<FakeDoor, Long> {
}
