package com.playkuround.playkuroundserver.domain.user.domain;

import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
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

    public static HighestScore createAllZero() {
        HighestScore highestScore = new HighestScore();
        highestScore.highestTotalScore = 0L;
        highestScore.highestQuizScore = 0L;
        highestScore.highestTimeScore = 0L;
        highestScore.highestMoonScore = 0L;
        highestScore.highestCardScore = 0L;
        highestScore.highestCatchScore = 0L;
        highestScore.highestHongBridgeScore = 0L;
        highestScore.highestAllClearScore = 0L;
        highestScore.highestMicrobeScore = 0L;
        return highestScore;
    }

    public void updateGameHighestScore(ScoreType scoreType, Long score) {
        switch (scoreType) {
            case QUIZ -> updateHighestQuizScore(score);
            case TIME -> updateHighestTimeScore(score);
            case MOON -> updateHighestMoonScore(score);
            case BOOK -> updateHighestCardScore(score);
            case CATCH -> updateHighestCatchScore(score);
            case CUPID -> updateHighestHongBridgeScore(score);
            case ALL_CLEAR -> updateHighestAllClearScore(score);
            case SURVIVE -> updateHighestMicrobeScore(score);
        }
    }

    public void updateHighestTotalScore(Long highestTotalScore) {
        if (this.highestTotalScore == null || this.highestTotalScore < highestTotalScore) {
            this.highestTotalScore = highestTotalScore;
        }
    }

    private void updateHighestQuizScore(Long highestQuizScore) {
        if (this.highestQuizScore == null || this.highestQuizScore < highestQuizScore) {
            this.highestQuizScore = highestQuizScore;
        }
    }

    private void updateHighestTimeScore(Long highestTimeScore) {
        if (this.highestTimeScore == null || this.highestTimeScore < highestTimeScore) {
            this.highestTimeScore = highestTimeScore;
        }
    }

    private void updateHighestMoonScore(Long highestMoonScore) {
        if (this.highestMoonScore == null || this.highestMoonScore < highestMoonScore) {
            this.highestMoonScore = highestMoonScore;
        }
    }

    private void updateHighestCardScore(Long highestCardScore) {
        if (this.highestCardScore == null || this.highestCardScore < highestCardScore) {
            this.highestCardScore = highestCardScore;
        }
    }

    private void updateHighestCatchScore(Long highestCatchScore) {
        if (this.highestCatchScore == null || this.highestCatchScore < highestCatchScore) {
            this.highestCatchScore = highestCatchScore;
        }
    }

    private void updateHighestHongBridgeScore(Long highestHongBridgeScore) {
        if (this.highestHongBridgeScore == null || this.highestHongBridgeScore < highestHongBridgeScore) {
            this.highestHongBridgeScore = highestHongBridgeScore;
        }
    }

    private void updateHighestAllClearScore(Long highestAllClearScore) {
        if (this.highestAllClearScore == null || this.highestAllClearScore < highestAllClearScore) {
            this.highestAllClearScore = highestAllClearScore;
        }
    }

    private void updateHighestMicrobeScore(Long highestMicrobeScore) {
        if (this.highestMicrobeScore == null || this.highestMicrobeScore < highestMicrobeScore) {
            this.highestMicrobeScore = highestMicrobeScore;
        }
    }
}
