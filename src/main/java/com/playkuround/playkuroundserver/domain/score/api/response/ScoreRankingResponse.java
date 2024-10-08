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
public class ScoreRankingResponse {

    private MyRank myRank;
    private final List<RankList> rank = new ArrayList<>();

    public static ScoreRankingResponse createEmptyResponse() {
        return new ScoreRankingResponse();
    }

    public void addRank(String nickname, int score, BadgeType profileBadgeType) {
        String badgeTypeName = profileBadgeType != null ? profileBadgeType.name() : null;
        this.rank.add(new RankList(nickname, badgeTypeName, score));
    }

    public void setMyRank(int ranking, int score, BadgeType profileBadgeType) {
        String badgeTypeName = profileBadgeType != null ? profileBadgeType.name() : null;
        this.myRank = new MyRank(ranking, score, badgeTypeName);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RankList {
        private String nickname;
        private String profileBadge;
        private int score;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyRank {
        private int ranking;
        private int score;
        private String profileBadge;
    }
}
