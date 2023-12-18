package com.playkuround.playkuroundserver.domain.auth.token.dao;

import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthVerifyTokenRepository extends JpaRepository<AuthVerifyToken, String> {

    boolean existsByAuthVerifyToken(String authVerifyToken);

    Optional<AuthVerifyToken> findByAuthVerifyToken(String authVerifyToken);
}
