package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.BadgeFindDto;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public void registerBadge(User user, String badgeType) {
        Badge badge = Badge.createBadge(user, BadgeType.valueOf(badgeType));
        badgeRepository.save(badge);
    }

    public List<BadgeFindDto> findBadgeByEmail(User user) {
        return badgeRepository.findByUser(user).stream()
                .map(BadgeFindDto::of)
                .collect(Collectors.toList());
    }
}
