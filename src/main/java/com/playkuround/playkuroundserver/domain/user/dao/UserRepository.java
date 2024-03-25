package com.playkuround.playkuroundserver.domain.user.dao;

import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.EmailAndNickname;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    @Query("select new com.playkuround.playkuroundserver.domain.user.dto.EmailAndNickname(u.email, u.nickname) " +
            "from User u " +
            "where u.email in :emails")
    List<EmailAndNickname> findNicknameByEmailIn(List<String> emails);
}
