package com.playkuround.playkuroundserver.domain.adventure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserScore {
    private final Long userId;
    private final Integer score;
    private final String nickname;
}
