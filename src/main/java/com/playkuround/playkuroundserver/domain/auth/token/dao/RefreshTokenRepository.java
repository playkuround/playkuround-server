package com.playkuround.playkuroundserver.domain.auth.token.dao;

import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    boolean existsByUserEmail(String userEmail);

    Optional<RefreshToken> findByUserEmail(String userEmail);

    void deleteByUserEmail(String userEmail);
}
