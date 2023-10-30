package com.playkuround.playkuroundserver.domain.auth.token.dao;

import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByUserEmail(String userEmail);

    boolean existsByUserEmail(String userEmail);

}
