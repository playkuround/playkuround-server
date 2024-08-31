package com.playkuround.playkuroundserver.domain.badge.application.college_special_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CollegeSpecialBadge {

    private final List<CollegeSpecialBadgeService> collegeSpecialBadgeServices;

    public CollegeSpecialBadge(List<CollegeSpecialBadgeService> collegeSpecialBadgeServices) {
        this.collegeSpecialBadgeServices = collegeSpecialBadgeServices;
    }

    public List<BadgeType> getBadgeTypes(User user, Landmark landmark) {
        return collegeSpecialBadgeServices.stream()
                .filter(service -> service.supports(landmark.getName()))
                .map(service -> service.getBadgeType(user, landmark).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }
}
