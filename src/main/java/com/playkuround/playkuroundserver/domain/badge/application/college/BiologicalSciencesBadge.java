package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

class BiologicalSciencesBadge implements CollegeBadge {

    BiologicalSciencesBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.동물생명과학관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_BIOLOGICAL_SCIENCES;
    }
}
