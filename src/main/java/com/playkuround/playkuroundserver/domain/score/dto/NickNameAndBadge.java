package com.playkuround.playkuroundserver.domain.score.dto;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

public record NickNameAndBadge(String nickname, BadgeType badgeType) {
}
