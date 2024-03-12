package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

public class EngineeringBadge implements CollegeBadge {

    protected EngineeringBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.공학관A ||
                landmarkType == LandmarkType.공학관B ||
                landmarkType == LandmarkType.공학관C ||
                landmarkType == LandmarkType.신공학관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_ENGINEERING;
    }
}
