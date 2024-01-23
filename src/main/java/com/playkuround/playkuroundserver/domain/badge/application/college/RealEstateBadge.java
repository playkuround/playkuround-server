package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

public class RealEstateBadge implements CollegeBadge {

    protected RealEstateBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.부동산학관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_REAL_ESTATE;
    }
}
