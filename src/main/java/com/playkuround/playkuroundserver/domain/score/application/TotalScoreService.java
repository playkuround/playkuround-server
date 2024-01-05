package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.dto.MyScore;
import com.playkuround.playkuroundserver.domain.adventure.dto.UserScore;
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
public class TotalScoreService {
    /**
     * TODO. 테스트 필요!!!!!!!!!!!!!!!!!!!!!!!!
     */

    private final UserRepository userRepository;
    private final AdventureRepository adventureRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String redisSetKey = "ranking";

    @Transactional
    public Long saveScore(User user, Long score) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.incrementScore(redisSetKey, user.getEmail(), score);

        Double myTotalScore = zSetOperations.score(redisSetKey, user.getEmail());
        if (myTotalScore == null) {
            return score; // 발생하지 않음
        }
        return myTotalScore.longValue();
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

    public ScoreRankingResponse getRankTop100ByLandmark(User user, Long landmarkId) {
        List<UserScore> userScores = adventureRepository.findUserScoreRankDescByLandmarkId(landmarkId);
        ScoreRankingResponse response = new ScoreRankingResponse();
        userScores.forEach(userScore -> {
            response.addRank(userScore.getNickname(), userScore.getScore());
        });

        Optional<MyScore> optionalMyScore = adventureRepository.findMyRankByLandmarkId(user, landmarkId);
        if (optionalMyScore.isPresent()) {
            MyScore myScore = optionalMyScore.get();
            response.setMyRank(myScore.getRank(), myScore.getScore());
        }
        return response;
    }
}
