package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

class SocialSciencesBadge implements CollegeBadge {

    SocialSciencesBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.상허연구관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_SOCIAL_SCIENCES;
    }
}
