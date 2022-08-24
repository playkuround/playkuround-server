package com.playkuround.playkuroundserver.domain.badge.dao;

import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.exception.BadgeNotFoundException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeFindDao {

    private final BadgeRepository badgeRepository;

    public Badge findByUser(User user) {
        return badgeRepository.findByUser(user)
                .orElseThrow(() -> new BadgeNotFoundException(user.getEmail()));
    }

}
