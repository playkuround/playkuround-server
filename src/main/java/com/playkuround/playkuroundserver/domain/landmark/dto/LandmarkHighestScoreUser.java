package com.playkuround.playkuroundserver.domain.landmark.dto;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

public record LandmarkHighestScoreUser(long score, String nickname, BadgeType badgeType) {
}
