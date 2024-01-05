package com.playkuround.playkuroundserver.domain.user.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HighestScore {

    private Long highestTotalScore;
    private Long highestQuizScore;
    private Long highestTimeScore;
    private Long highestMoonScore;
    private Long highestCardScore;
    private Long highestCatchScore;
    private Long highestHongBridgeScore;
    private Long highestAllClearScore;
    private Long highestMicrobeScore;
}
