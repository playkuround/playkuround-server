package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dao.ScoreRepository;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.score.dto.MyRank;
import com.playkuround.playkuroundserver.domain.score.dto.RankData;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
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
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(redisSetKey, 0, 99);

        List<RankData> rankDataList = getPresentRankDataList(typedTuples);
        Map<String, String> emailBindingNickname = getNicknameBindingEmailMapList(rankDataList);

        ScoreRankingResponse response = ScoreRankingResponse.createEmptyResponse();
        rankDataList.forEach(rankData -> {
            String nickname = emailBindingNickname.get(rankData.getEmail());
            response.addRank(nickname, rankData.getScore());
        });

        MyRank myRank = getMyRank(user);
        response.setMyRank(myRank.getRanking(), myRank.getScore());

        return response;
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

    private MyRank getMyRank(User user) {
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();

        Double myTotalScore = ZSetOperations.score(redisSetKey, user.getEmail());
        Set<String> emailSet = ZSetOperations.reverseRangeByScore(redisSetKey, myTotalScore, myTotalScore, 0, 1);

        int myRank = -1;
        for (String email : emailSet) {
            myRank = ZSetOperations.reverseRank(redisSetKey, email).intValue();
        }
        return new MyRank(myRank + 1, myTotalScore.intValue());
    }
}
