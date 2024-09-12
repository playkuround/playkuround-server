package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

class SciencesBadge implements CollegeBadge {

    SciencesBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.과학관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_SCIENCES;
    }
}
