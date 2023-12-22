package com.playkuround.playkuroundserver.domain.score.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ScoreRankingResponse {

    private MyRank myRank;
    private List<PresentRankData> rank = new ArrayList<>();

    public static ScoreRankingResponse createEmptyResponse() {
        return new ScoreRankingResponse();
    }

    public void addRank(String nickname, int score) {
        this.rank.add(new PresentRankData(nickname, score));
    }

    public void setMyRank(int ranking, int score) {
        this.myRank = new MyRank(ranking, score);
    }

    @Getter
    @AllArgsConstructor
    public static class PresentRankData {
        private String nickname;
        private int score;
    }

    @Getter
    @AllArgsConstructor
    public static class MyRank {
        private int ranking;
        private int score;
    }
}
