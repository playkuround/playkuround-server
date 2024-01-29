package com.playkuround.playkuroundserver.domain.badge.application.college_special_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

@Component
class ArtAndDesignBadgeService implements CollegeSpecialBadgeService {

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return LandmarkType.예디대.equals(landmarkType) ||
                LandmarkType.공예관.equals(landmarkType);
    }

    @Override
    public Optional<BadgeType> getBadgeType(User user, Set<BadgeType> userHadBadgeSet, Landmark landmark) {
        LocalTime now = LocalTime.now();
        if (now.isAfter(LocalTime.of(9, 0)) && now.isBefore(LocalTime.of(12, 0))) {
            if (!userHadBadgeSet.contains(BadgeType.COLLEGE_OF_ART_AND_DESIGN_BEFORE_NOON)) {
                return Optional.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN_BEFORE_NOON);
            }
        }
        else if (now.isAfter(LocalTime.of(12, 0)) && now.isBefore(LocalTime.of(18, 0))) {
            if (!userHadBadgeSet.contains(BadgeType.COLLEGE_OF_ART_AND_DESIGN_AFTER_NOON)) {
                return Optional.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN_AFTER_NOON);
            }
        }
        else if (now.isAfter(LocalTime.of(23, 0)) || now.isBefore(LocalTime.of(4, 0))) {
            if (!userHadBadgeSet.contains(BadgeType.COLLEGE_OF_ART_AND_DESIGN_NIGHT)) {
                return Optional.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN_NIGHT);
            }
        }
        return Optional.empty();
    }
}
