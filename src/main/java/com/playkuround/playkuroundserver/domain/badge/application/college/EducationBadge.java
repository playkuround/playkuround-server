package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

public class EducationBadge implements CollegeBadge {

    protected EducationBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.교육과학관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_EDUCATION;
    }
}
