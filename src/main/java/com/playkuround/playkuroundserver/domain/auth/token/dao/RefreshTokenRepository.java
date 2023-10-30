package com.playkuround.playkuroundserver.domain.auth.token.dao;

import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    boolean existsByUserEmail(String userEmail);

}
