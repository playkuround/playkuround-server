package com.playkuround.playkuroundserver.domain.landmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LandmarkHighestScoreUser {

    private Long score;
    private String nickname;
    private boolean hasResult;

    public static LandmarkHighestScoreUser createEmpty() {
        return new LandmarkHighestScoreUser();
    }

    public static LandmarkHighestScoreUser of(String nickname, Long score) {
        return new LandmarkHighestScoreUser(score, nickname, true);
    }
}
