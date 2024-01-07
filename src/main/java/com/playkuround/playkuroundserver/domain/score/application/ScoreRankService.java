package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dto.RankData;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
import lombok.Setter;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScoreRankService {

    private final List<RankData> rankDataList;
    @Setter
    private Map<String, String> emailBindingNickname;

    public ScoreRankService(Set<ZSetOperations.TypedTuple<String>> typedTuples) {
        this.rankDataList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            RankData rankData = new RankData(typedTuple.getValue(), typedTuple.getScore().intValue());
            rankDataList.add(rankData);
        }
    }

    public List<String> getRankUserEmails() {
        return rankDataList.stream()
                .map(RankData::getEmail)
                .toList();
    }

    public ScoreRankingResponse createScoreRankingResponse() {
        if (emailBindingNickname == null) {
            throw new IllegalArgumentException("emailBindingNickname must not be null");
        }

        ScoreRankingResponse response = ScoreRankingResponse.createEmptyResponse();
        rankDataList.forEach(rankData -> {
            String nickname = emailBindingNickname.get(rankData.getEmail());
            response.addRank(nickname, rankData.getScore());
        });
        return response;
    }
}
