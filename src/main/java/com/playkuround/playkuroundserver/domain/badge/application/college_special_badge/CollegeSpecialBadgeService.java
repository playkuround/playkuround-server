package com.playkuround.playkuroundserver.domain.badge.application.college_special_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.domain.User;

import java.util.Optional;

public interface CollegeSpecialBadgeService {

    boolean supports(LandmarkType landmarkType);

    Optional<BadgeType> getBadgeType(User user, Landmark landmark);
}
