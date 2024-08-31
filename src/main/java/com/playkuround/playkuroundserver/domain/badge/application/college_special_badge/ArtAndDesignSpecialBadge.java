package com.playkuround.playkuroundserver.domain.badge.application.college_special_badge;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class ArtAndDesignSpecialBadge implements CollegeSpecialBadgeService {

    private final DateTimeService dateTimeService;

    @Override
    public boolean supports(LandmarkType landmarkType) {
        return LandmarkType.예디대 == landmarkType || LandmarkType.공예관 == landmarkType;
    }

    @Override
    public Optional<BadgeType> getBadgeType(User user, Landmark landmark) {
        LocalDateTime nowDateTime = dateTimeService.getLocalDateTimeNow();
        LocalTime now = nowDateTime.toLocalTime();

        if (now.isAfter(LocalTime.of(9, 0)) && now.isBefore(LocalTime.of(12, 0))) {
            return Optional.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN_BEFORE_NOON);
        }
        if (now.isAfter(LocalTime.of(12, 0)) && now.isBefore(LocalTime.of(18, 0))) {
            return Optional.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN_AFTER_NOON);
        }
        if (now.isAfter(LocalTime.of(23, 0)) || now.isBefore(LocalTime.of(4, 0))) {
            return Optional.of(BadgeType.COLLEGE_OF_ART_AND_DESIGN_NIGHT);
        }
        return Optional.empty();
    }
}
