package com.playkuround.playkuroundserver.domain.fakedoor.dao;

import com.playkuround.playkuroundserver.domain.fakedoor.domain.FakeDoor;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FakeDoorRepository extends JpaRepository<FakeDoor, Long> {
    void deleteByUser(User user);
}
