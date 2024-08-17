package com.playkuround.playkuroundserver.domain.score.api.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScoreRankingResponse {

    private MyRank myRank;
    private final List<RankList> rank = new ArrayList<>();

    public static ScoreRankingResponse createEmptyResponse() {
        return new ScoreRankingResponse();
    }

    public void addRank(String nickname, int score) {
        this.rank.add(new RankList(nickname, score));
    }

    public void setMyRank(int ranking, int score) {
        this.myRank = new MyRank(ranking, score);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RankList {
        private String nickname;
        private int score;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyRank {
        private int ranking;
        private int score;
    }
}
