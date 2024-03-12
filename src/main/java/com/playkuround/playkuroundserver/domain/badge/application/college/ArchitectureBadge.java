package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

public class ArchitectureBadge implements CollegeBadge {

    protected ArchitectureBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.건축관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_ARCHITECTURE;
    }
}
