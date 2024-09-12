package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

class InstituteTechnologyBadge implements CollegeBadge {

    InstituteTechnologyBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.공학관A ||
                landmarkType == LandmarkType.공학관B ||
                landmarkType == LandmarkType.공학관C ||
                landmarkType == LandmarkType.생명과학관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY;
    }
}
