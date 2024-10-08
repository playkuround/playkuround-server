package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

class AdministrationBadge implements CollegeBadge {

    AdministrationBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.경영관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION;
    }
}
