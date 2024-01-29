package com.playkuround.playkuroundserver.domain.badge.application.college_special_badge;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
class EngineeringBadgeService implements CollegeSpecialBadgeService {

    private final Map<LandmarkType, BadgeAndRequiredCount> landmarkTypeAndBadge = Map.of(
            LandmarkType.공학관A, new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_ENGINEERING_A, 10),
            LandmarkType.공학관B, new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_ENGINEERING_B, 10),
            LandmarkType.공학관C, new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_ENGINEERING_C, 10)
    );

    private final AdventureRepository adventureRepository;

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return LandmarkType.공학관A.equals(landmarkType) ||
                LandmarkType.공학관B.equals(landmarkType) ||
                LandmarkType.공학관C.equals(landmarkType);
    }

    @Override
    public Optional<BadgeType> getBadgeType(User user, Set<BadgeType> userHadBadgeSet, Landmark landmark) {
        BadgeAndRequiredCount badgeAndRequiredCount = landmarkTypeAndBadge.get(landmark.getName());
        if (badgeAndRequiredCount != null) {
            if (!userHadBadgeSet.contains(badgeAndRequiredCount.badgeType) &&
                    adventureRepository.countByUserAndLandmark(user, landmark) == badgeAndRequiredCount.requiredCount) {
                return Optional.of(badgeAndRequiredCount.badgeType);
            }
        }
        return Optional.empty();
    }

    private record BadgeAndRequiredCount(BadgeType badgeType, int requiredCount) {
    }
}
