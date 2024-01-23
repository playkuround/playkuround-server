package com.playkuround.playkuroundserver.domain.badge.application.college;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;

public class VeterinaryMedicineBadge implements CollegeBadge {

    protected VeterinaryMedicineBadge() {
    }

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return landmarkType == LandmarkType.수의학관;
    }

    @Override
    public BadgeType getBadge() {
        return BadgeType.COLLEGE_OF_VETERINARY_MEDICINE;
    }
}
