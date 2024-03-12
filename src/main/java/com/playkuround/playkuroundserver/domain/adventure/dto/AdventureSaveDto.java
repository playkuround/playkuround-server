package com.playkuround.playkuroundserver.domain.adventure.dto;

import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.Location;

public record AdventureSaveDto(User user, Long landmarkId, Location requestLocation, long score, ScoreType scoreType) {
}
