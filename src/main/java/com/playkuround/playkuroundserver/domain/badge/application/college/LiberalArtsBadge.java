package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

public class LiberalArtsBadge implements CollegeBadge {

    protected LiberalArtsBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.인문학관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_LIBERAL_ARTS;
    }
}
