package com.playkuround.playkuroundserver.domain.badge.application.business_administration;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessAdministrationBadgeList {

    @Getter
    private static final List<BadgeAndRequiredCount> badges = List.of(
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_10, 10),
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_30, 30),
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_50, 50),
            new BadgeAndRequiredCount(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_70, 70)
    );


    public record BadgeAndRequiredCount(BadgeType badgeType, int requiredCount) {
    }
}
