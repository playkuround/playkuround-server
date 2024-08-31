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

@Component
@RequiredArgsConstructor
class BusinessAdministrationSpecialBadge implements CollegeSpecialBadgeService {

    private final AdventureRepository adventureRepository;

    private final List<BadgeAndRequiredCount> badgeAndRequiredCounts = List.of(
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_10, 10),
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_30, 30),
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_50, 50),
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_70, 70)
    );

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return LandmarkType.경영관 == landmarkType;
    }

    @Override
    public Optional<BadgeType> getBadgeType(User user, Landmark landmark) {
        long visitedNumber = adventureRepository.countByUserAndLandmark(user, landmark);
        return badgeAndRequiredCounts.stream()
                .filter(badgeAndRequiredCount -> badgeAndRequiredCount.requiredCount == visitedNumber)
                .findFirst()
                .map(BadgeAndRequiredCount::badgeType);
    }

    private record BadgeAndRequiredCount(BadgeType badgeType, int requiredCount) {
    }
}
