package com.playkuround.playkuroundserver.domain.score.api.response;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TotalScoreRankingResponse {

    private MyRank myRank;
    private final List<RankList> rank = new ArrayList<>();

    public static TotalScoreRankingResponse createEmptyResponse() {
        return new TotalScoreRankingResponse();
    }

    public void addRank(String nickname, int score, BadgeType badgeType) {
        String badgeTypeName = badgeType != null ? badgeType.name() : null;
        this.rank.add(new RankList(nickname, badgeTypeName, score));
    }

    public void setMyRank(int ranking, int score, BadgeType badgeType) {
        String badgeTypeName = badgeType != null ? badgeType.name() : null;
        this.myRank = new MyRank(ranking, score, badgeTypeName);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RankList {
        private String nickname;
        private String badgeType;
        private int score;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyRank {
        private int ranking;
        private int score;
        private String badgeType;
    }
}
