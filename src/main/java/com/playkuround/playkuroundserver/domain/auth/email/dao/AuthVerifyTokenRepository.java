package com.playkuround.playkuroundserver.domain.auth.email.dao;

import com.playkuround.playkuroundserver.domain.auth.email.domain.AuthVerifyToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthVerifyTokenRepository extends CrudRepository<AuthVerifyToken, String> {

    Optional<AuthVerifyToken> findByAuthVerifyToken(String authVerifyToken);

}
