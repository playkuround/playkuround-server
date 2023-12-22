package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dao.ScoreRepository;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.score.dto.PresentRankData;
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

    public List<ScoreRankingResponse> getRankTop100() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(redisSetKey, 0, 99);

        List<PresentRankData> presentRankDataList = getPresentRankDataList(typedTuples);
        Map<String, String> emailBindingNickname = getNicknameBindingEmailMapList(presentRankDataList);

        return presentRankDataList.stream()
                .map(presentRankData -> {
                    String nickname = emailBindingNickname.get(presentRankData.getEmail());

                    return ScoreRankingResponse.builder()
                            .nickname(nickname)
                            .score(presentRankData.getScore())
                            .build();
                })
                .toList();
    }

    private List<PresentRankData> getPresentRankDataList(Set<ZSetOperations.TypedTuple<String>> typedTuples) {
        List<PresentRankData> presentRankDataList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            PresentRankData presentRankData = new PresentRankData(typedTuple.getValue(), typedTuple.getScore().intValue());
            presentRankDataList.add(presentRankData);
        }
        return presentRankDataList;
    }

    private Map<String, String> getNicknameBindingEmailMapList(List<PresentRankData> presentRankDataList) {
        List<String> emails = presentRankDataList.stream().map(PresentRankData::getEmail).toList();
        List<Map<String, String>> nicknameBindingEmailMapList = userRepository.findNicknameByEmailIn(emails);

        Map<String, String> nicknameBindingEmailMap = new HashMap<>();
        nicknameBindingEmailMapList
                .forEach(map -> nicknameBindingEmailMap.put(map.get("email"), map.get("nickname")));
        return nicknameBindingEmailMap;
    }

    public Long getMyRank(User user) {
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();

        Double myTotalScore = ZSetOperations.score(redisSetKey, user.getEmail());
        Set<String> emailSet = ZSetOperations.reverseRangeByScore(redisSetKey, myTotalScore, myTotalScore, 0, 1);
        Long myRank = -1L;
        for (String email : emailSet) {
            myRank = ZSetOperations.reverseRank(redisSetKey, email);
        }
        return myRank + 1;
    }

}
