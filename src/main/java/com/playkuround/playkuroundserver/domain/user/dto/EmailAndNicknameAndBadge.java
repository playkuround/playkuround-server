package com.playkuround.playkuroundserver.domain.user.dto;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

public record EmailAndNicknameAndBadge(String email, String nickname, BadgeType badgeType) {
}
