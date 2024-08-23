package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.api.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.score.dto.NickNameAndBadge;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

class ScoreRankService {

    private final List<RankData> rankDataList;

    public ScoreRankService(Set<ZSetOperations.TypedTuple<String>> typedTuples) {
        this.rankDataList = typedTuples.stream()
                .filter(typedTuple -> typedTuple.getValue() != null && typedTuple.getScore() != null)
                .map(typedTuple -> new RankData(typedTuple.getValue(), typedTuple.getScore().intValue()))
                .toList();
    }

    public List<String> getRankUserEmails() {
        return rankDataList.stream()
                .map(RankData::email)
                .toList();
    }

    public ScoreRankingResponse createScoreRankingResponse(Map<String, NickNameAndBadge> emailBindingData) {
        ScoreRankingResponse response = ScoreRankingResponse.createEmptyResponse();
        rankDataList.forEach(rankData -> {
            NickNameAndBadge nickNameAndBadge = emailBindingData.get(rankData.email);
            if (nickNameAndBadge == null) {
                throw new IllegalArgumentException("Not found nickname and badge for email: " + rankData.email);
            }
            response.addRank(nickNameAndBadge.nickname(), rankData.score, nickNameAndBadge.badgeType());
        });
        return response;
    }

    record RankData(String email, int score) {
    }
}
