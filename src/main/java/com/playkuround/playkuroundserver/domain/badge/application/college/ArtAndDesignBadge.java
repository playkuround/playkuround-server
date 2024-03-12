package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

public class ArtAndDesignBadge implements CollegeBadge {

    protected ArtAndDesignBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.예디대 ||
                landmarkType == LandmarkType.공예관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_ART_AND_DESIGN;
    }
}
