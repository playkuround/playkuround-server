package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dao.ScoreRepository;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        ScoreRankService scoreRankService = new ScoreRankService(typedTuples);
        ScoreRankingResponse response = setTop100(scoreRankService);

        setMyRank(user, response);
        return response;
    }

    private ScoreRankingResponse setTop100(ScoreRankService scoreRankService) {
        List<String> emails = scoreRankService.getRankUserEmails();
        Map<String, String> emailBindingNickname = getNicknameBindingEmailMapList(emails);
        ScoreRankingResponse response = scoreRankService.createScoreRankingResponse(emailBindingNickname);
        return response;
    }

    private Map<String, String> getNicknameBindingEmailMapList(List<String> emails) {
        List<Map<String, String>> nicknameBindingEmailMapList = userRepository.findNicknameByEmailIn(emails);

        Map<String, String> nicknameBindingEmailMap = new HashMap<>();
        nicknameBindingEmailMapList
                .forEach(map -> nicknameBindingEmailMap.put(map.get("email"), map.get("nickname")));
        return nicknameBindingEmailMap;
    }

    private void setMyRank(User user, ScoreRankingResponse response) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Double myTotalScore = zSetOperations.score(redisSetKey, user.getEmail());
        if (myTotalScore == null) {
            response.setMyRank(0, 0);
            return;
        }

        Set<String> emailSet = zSetOperations.reverseRangeByScore(redisSetKey, myTotalScore, myTotalScore, 0, 1);
        int myRank = -1;
        for (String email : emailSet) {
            myRank = zSetOperations.reverseRank(redisSetKey, email).intValue();
        }

        response.setMyRank(myRank + 1, myTotalScore.intValue());
    }
}
