package com.playkuround.playkuroundserver.domain.score.dto;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

public record NicknameAndScoreAndBadgeType(String nickname, int score, BadgeType badgeType) {
}
