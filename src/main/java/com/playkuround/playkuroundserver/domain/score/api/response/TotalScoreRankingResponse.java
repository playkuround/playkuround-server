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

    private final List<RankList> rank = new ArrayList<>();
    private MyRank myRank;

    public static TotalScoreRankingResponse createEmptyResponse() {
        return new TotalScoreRankingResponse();
    }

    public void addRank(String nickname, int score, BadgeType badgeType) {
        if (badgeType != null) {
            this.rank.add(new RankList(nickname, badgeType.name(), score));
        }
        else {
            this.rank.add(new RankList(nickname, null, score));
        }
    }

    public void setMyRank(int ranking, int score, BadgeType badgeType) {
        if (badgeType != null) {
            this.myRank = new MyRank(ranking, score, badgeType.name());
        }
        else {
            this.myRank = new MyRank(ranking, score, null);
        }
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