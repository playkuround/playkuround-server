package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.dto.request.BadgeFindRequest;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;

    public List<BadgeFindRequest> findBadgeByEmail(User user) {
        return badgeRepository.findByUser(user).stream()
                .map(BadgeFindRequest::from)
                .toList();
    }
}
