package com.playkuround.playkuroundserver.domain.auth.token.dao;

import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    List<RefreshToken> findAllByUserEmail(String userEmail);

}
