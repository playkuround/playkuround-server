package com.playkuround.playkuroundserver.domain.user.dao;

import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    @Query("select new map(u.email as email, u.nickname as nickname) from User u where u.email in :emails")
    List<Map<String, String>> findNicknameByEmailIn(List<String> emails);
}
