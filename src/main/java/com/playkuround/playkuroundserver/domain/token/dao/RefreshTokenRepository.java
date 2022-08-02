package com.playkuround.playkuroundserver.domain.token.dao;

import com.playkuround.playkuroundserver.domain.token.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
