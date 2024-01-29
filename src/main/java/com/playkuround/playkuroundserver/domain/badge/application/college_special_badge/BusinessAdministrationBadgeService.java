package com.playkuround.playkuroundserver.domain.badge.application.college_special_badge;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
class BusinessAdministrationBadgeService implements CollegeSpecialBadgeService {

    private final List<BadgeAndRequiredCount> badgeAndRequiredCounts = List.of(
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_10, 10),
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_30, 30),
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_50, 50),
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_70, 70)
    );

    private final AdventureRepository adventureRepository;

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return LandmarkType.경영관.equals(landmarkType);
    }

    @Override
    public Optional<BadgeType> getBadgeType(User user, Set<BadgeType> userHadBadgeSet, Landmark landmark) {
        for (BadgeAndRequiredCount badgeAndRequiredCount : badgeAndRequiredCounts) {
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
