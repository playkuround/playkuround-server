package com.playkuround.playkuroundserver.domain.adventure.dto;

import com.playkuround.playkuroundserver.domain.user.domain.User;

public record UserAndScore(User user, long score) {
}
