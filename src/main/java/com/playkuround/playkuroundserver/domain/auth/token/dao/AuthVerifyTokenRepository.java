package com.playkuround.playkuroundserver.domain.auth.token.dao;

import com.playkuround.playkuroundserver.domain.auth.token.domain.AuthVerifyToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthVerifyTokenRepository extends CrudRepository<AuthVerifyToken, String> {

    Optional<AuthVerifyToken> findByAuthVerifyToken(String authVerifyToken);

}
