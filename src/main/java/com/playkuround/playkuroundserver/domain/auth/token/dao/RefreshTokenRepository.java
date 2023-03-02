package com.playkuround.playkuroundserver.domain.auth.token.dao;

import com.playkuround.playkuroundserver.domain.auth.token.domain.RefreshToken;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    List<RefreshToken> findAllByUserEmail(String userEmail);

    Optional<RefreshToken> findByUser(User user);

}
