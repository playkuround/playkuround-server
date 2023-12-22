package com.playkuround.playkuroundserver.domain.score.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ScoreRankingResponse {

    private String nickname;
    private Integer score;
}
