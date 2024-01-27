package com.playkuround.playkuroundserver.domain.badge.application.engineering;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EngineeringBadgeMap {

    private static final Map<LandmarkType, BadgeAndRequiredCount> landmarkTypeAndBadge = Map.of(
            LandmarkType.공학관A, new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_ENGINEERING_A, 10),
            LandmarkType.공학관B, new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_ENGINEERING_B, 10),
            LandmarkType.공학관C, new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_ENGINEERING_C, 10)
    );

    public static BadgeAndRequiredCount findBadgeByLandmarkType(LandmarkType landmarkType) {
        return landmarkTypeAndBadge.get(landmarkType);
    }

    public record BadgeAndRequiredCount(BadgeType badgeType, int requiredCount) {
    }
}
