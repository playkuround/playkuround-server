package com.playkuround.playkuroundserver.domain.badge.application.college_special_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class CollegeSpecialBadgeFactory {

    private final List<CollegeSpecialBadgeService> collegeSpecialBadgeServices;

    public CollegeSpecialBadgeFactory(List<CollegeSpecialBadgeService> collegeSpecialBadgeServices) {
        this.collegeSpecialBadgeServices = collegeSpecialBadgeServices;
    }

    public Optional<BadgeType> getBadgeType(User user, Set<BadgeType> userHadBadgeSet, Landmark landmark) {
        for (CollegeSpecialBadgeService collegeSpecialBadgeService : collegeSpecialBadgeServices) {
            if (collegeSpecialBadgeService.supports(landmark.getName())) {
                return collegeSpecialBadgeService.getBadgeType(user, userHadBadgeSet, landmark);
            }
        }
        return Optional.empty();
    }
}
