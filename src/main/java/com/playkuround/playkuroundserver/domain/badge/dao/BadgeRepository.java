package com.playkuround.playkuroundserver.domain.badge.dao;

import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByUser(User user);

    boolean existsByUserAndBadgeType(User user, BadgeType badgeType);

    void deleteByUser(User user);
}
