package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
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
public class TotalScoreService {

    private final String redisSetKey = "ranking";
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public Long incrementTotalScore(User user, Long score) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.incrementScore(redisSetKey, user.getEmail(), score);

        Double myTotalScore = zSetOperations.score(redisSetKey, user.getEmail());
        if (myTotalScore == null) {
            return score; // 발생하지 않음
        }
        user.getHighestScore().updateHighestTotalScore(myTotalScore.longValue());
        return myTotalScore.longValue();
    }

    @Transactional(readOnly = true)
    public ScoreRankingResponse getRankTop100(User user) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples
                = redisTemplate.opsForZSet().reverseRangeWithScores(redisSetKey, 0, 99);
        if (typedTuples == null) {
            return ScoreRankingResponse.createEmptyResponse();
        }

        ScoreRankService scoreRankService = new ScoreRankService(typedTuples);
        Map<String, String> emailBindingNickname = getNicknameBindingEmailMapList(scoreRankService.getRankUserEmails());
        scoreRankService.setEmailBindingNickname(emailBindingNickname);

        ScoreRankingResponse response = scoreRankService.createScoreRankingResponse();

        RankAndScore myRank = getMyRank(user);
        response.setMyRank(myRank.ranking(), myRank.score());

        return response;
    }

    private Map<String, String> getNicknameBindingEmailMapList(List<String> emails) {
        List<Map<String, String>> nicknameBindingEmailMapList = userRepository.findNicknameByEmailIn(emails);
        return nicknameBindingEmailMapList.stream()
                .collect(HashMap::new, (m, v) -> m.put(v.get("email"), v.get("nickname")), HashMap::putAll);
    }

    private RankAndScore getMyRank(User user) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Double myTotalScore = zSetOperations.score(redisSetKey, user.getEmail());
        if (myTotalScore == null) {
            return new RankAndScore(0, 0);
        }

        Set<String> emailSet = zSetOperations.reverseRangeByScore(redisSetKey, myTotalScore, myTotalScore, 0, 1);
        if (emailSet == null) {
            return new RankAndScore(0, 0);
        }

        int myRank = -1;
        for (String email : emailSet) {
            Long rank = zSetOperations.reverseRank(redisSetKey, email);
            if (rank == null) {
                continue;
            }
            myRank = rank.intValue();
        }

        return new RankAndScore(myRank + 1, myTotalScore.intValue());
    }
}
