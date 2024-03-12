package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
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

    public ScoreRankingResponse createScoreRankingResponse(Map<String, String> emailBindingNickname) {
        ScoreRankingResponse response = ScoreRankingResponse.createEmptyResponse();
        rankDataList.forEach(rankData -> {
            String nickname = emailBindingNickname.get(rankData.email);
            if (nickname == null) {
                throw new IllegalArgumentException("rank nickname is null");
            }
            response.addRank(nickname, rankData.score);
        });
        return response;
    }

    record RankData(String email, int score) {
    }
}
