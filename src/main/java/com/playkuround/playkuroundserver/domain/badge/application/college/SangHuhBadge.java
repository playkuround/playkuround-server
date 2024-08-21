package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

public class SangHuhBadge implements CollegeBadge {

    protected SangHuhBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.산학협동관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_SANG_HUH;
    }
}
