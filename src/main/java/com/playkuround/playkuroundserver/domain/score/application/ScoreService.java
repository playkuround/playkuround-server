package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dao.ScoreRepository;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.score.dto.RankData;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String redisSetKey = "ranking";

    @Transactional
    public int saveScore(User user, ScoreType scoreType, Integer score) {
        int correctScore = convertCorrectScore(scoreType, score);
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.incrementScore(redisSetKey, user.getEmail(), correctScore);
        return correctScore;
    }

    private int convertCorrectScore(ScoreType scoreType, Integer score) {
        // TODO 가중치 적용
        return score;
    }

    public ScoreRankingResponse getRankTop100(User user) {
        ScoreRankingResponse response = ScoreRankingResponse.createEmptyResponse();
        setRankTop100(response);
        setMyRank(user, response);
        return response;
    }

    private void setRankTop100(ScoreRankingResponse response) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(redisSetKey, 0, 99);

        List<RankData> rankDataList = getPresentRankDataList(typedTuples);
        Map<String, String> emailBindingNickname = getNicknameBindingEmailMapList(rankDataList);
        rankDataList.forEach(rankData -> {
            String nickname = emailBindingNickname.get(rankData.getEmail());
            response.addRank(nickname, rankData.getScore());
        });
    }

    private List<RankData> getPresentRankDataList(Set<ZSetOperations.TypedTuple<String>> typedTuples) {
        List<RankData> rankDataList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            RankData rankData = new RankData(typedTuple.getValue(), typedTuple.getScore().intValue());
            rankDataList.add(rankData);
        }
        return rankDataList;
    }

    private Map<String, String> getNicknameBindingEmailMapList(List<RankData> rankDataList) {
        List<String> emails = rankDataList.stream().map(RankData::getEmail).toList();
        List<Map<String, String>> nicknameBindingEmailMapList = userRepository.findNicknameByEmailIn(emails);

        Map<String, String> nicknameBindingEmailMap = new HashMap<>();
        nicknameBindingEmailMapList
                .forEach(map -> nicknameBindingEmailMap.put(map.get("email"), map.get("nickname")));
        return nicknameBindingEmailMap;
    }

    private void setMyRank(User user, ScoreRankingResponse response) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Double myTotalScore = zSetOperations.score(redisSetKey, user.getEmail());
        Set<String> emailSet = zSetOperations.reverseRangeByScore(redisSetKey, myTotalScore, myTotalScore, 0, 1);

        int myRank = -1;
        for (String email : emailSet) {
            myRank = zSetOperations.reverseRank(redisSetKey, email).intValue();
        }

        response.setMyRank(myRank + 1, myTotalScore.intValue());
    }
}
