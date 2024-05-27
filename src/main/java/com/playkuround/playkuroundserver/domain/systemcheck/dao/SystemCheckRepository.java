package com.playkuround.playkuroundserver.domain.systemcheck.dao;

import com.playkuround.playkuroundserver.domain.systemcheck.domain.SystemCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemCheckRepository extends JpaRepository<SystemCheck, Long> {

    Optional<SystemCheck> findTopByOrderByUpdatedAtDesc();
}
