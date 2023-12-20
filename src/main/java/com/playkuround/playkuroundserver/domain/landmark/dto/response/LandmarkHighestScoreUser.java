package com.playkuround.playkuroundserver.domain.landmark.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class LandmarkHighestScoreUser {
    private String nickname;
    private Long score;

    public static LandmarkHighestScoreUser createEmptyResponse() {
        return new LandmarkHighestScoreUser(null, null);
    }
}
