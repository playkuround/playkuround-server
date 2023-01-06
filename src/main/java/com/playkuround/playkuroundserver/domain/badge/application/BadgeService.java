package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.BadgeFindDto;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
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

    private final UserFindDao userFindDao;
    private final BadgeRepository badgeRepository;

    public void registerBadge(String userEmail, String badgeType) {
        User user = userFindDao.findByEmail(userEmail);
        Badge badge = Badge.createBadge(user, BadgeType.valueOf(badgeType));
        badgeRepository.save(badge);
    }

    public List<BadgeFindDto> findBadgeByEmail(String userEmail) {
        User user = userFindDao.findByEmail(userEmail);
        return badgeRepository.findByUser(user).stream()
                .map(BadgeFindDto::of)
                .collect(Collectors.toList());
    }
}
