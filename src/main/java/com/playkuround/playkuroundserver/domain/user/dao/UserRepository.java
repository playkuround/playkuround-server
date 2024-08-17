package com.playkuround.playkuroundserver.domain.user.dao;

import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.EmailAndNicknameAndBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    @Query("select new com.playkuround.playkuroundserver.domain.user.dto.EmailAndNicknameAndBadge(u.email, u.nickname, u.representBadge) " +
            "from User u " +
            "where u.email in :emails")
    List<EmailAndNicknameAndBadge> findNicknameByEmailIn(List<String> emails);

    List<User> findByEmailIn(List<String> emailList);
}
