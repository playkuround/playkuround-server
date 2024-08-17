package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.api.response.TotalScoreRankingResponse;
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

    public TotalScoreRankingResponse createScoreRankingResponse(Map<String, NickNameAndBadge> emailBindingData) {
        TotalScoreRankingResponse response = TotalScoreRankingResponse.createEmptyResponse();
        rankDataList.forEach(rankData -> {
            NickNameAndBadge nickNameAndBadge = emailBindingData.get(rankData.email);
            if (nickNameAndBadge.nickname() == null) {
                throw new IllegalArgumentException("rank nickname is null");
            }
            response.addRank(nickNameAndBadge.nickname(), rankData.score, nickNameAndBadge.badgeType());
        });
        return response;
    }

    record RankData(String email, int score) {
    }
}
