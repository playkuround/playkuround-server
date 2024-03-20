package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.EmailAndNickname;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TotalScoreService {

    private final String redisSetKey;
    private final UserRepository userRepository;
    private final ZSetOperations<String, String> zSetOperations;

    public TotalScoreService(UserRepository userRepository, RedisTemplate<String, String> redisTemplate) {
        this.redisSetKey = "ranking";
        this.userRepository = userRepository;
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    @Transactional
    public Long incrementTotalScore(User user, Long score) {
        zSetOperations.incrementScore(redisSetKey, user.getEmail(), score);

        Double myTotalScore = zSetOperations.score(redisSetKey, user.getEmail());
        if (myTotalScore == null) {
            return score; // 발생하지 않음
        }
        return myTotalScore.longValue();
    }

    @Transactional(readOnly = true)
    public ScoreRankingResponse getRankTop100(User user) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples
                = zSetOperations.reverseRangeWithScores(redisSetKey, 0, 99);
        if (typedTuples == null) {
            return ScoreRankingResponse.createEmptyResponse();
        }

        ScoreRankService scoreRankService = new ScoreRankService(typedTuples);
        Map<String, String> emailBindingNickname = getNicknameBindingEmailMapList(scoreRankService.getRankUserEmails());
        ScoreRankingResponse response = scoreRankService.createScoreRankingResponse(emailBindingNickname);

        RankAndScore myRank = getMyRank(user);
        response.setMyRank(myRank.ranking(), myRank.score());

        return response;
    }

    private Map<String, String> getNicknameBindingEmailMapList(List<String> emails) {
        List<EmailAndNickname> nicknameByEmailIn = userRepository.findNicknameByEmailIn(emails);
        return nicknameByEmailIn.stream()
                .collect(Collectors.toMap(EmailAndNickname::email, EmailAndNickname::nickname));
    }

    private RankAndScore getMyRank(User user) {
        Double myTotalScore = zSetOperations.score(redisSetKey, user.getEmail());
        if (myTotalScore == null) {
            return new RankAndScore(0, 0);
        }

        Set<String> emailSet = zSetOperations.reverseRangeByScore(redisSetKey, myTotalScore, myTotalScore, 0, 1);
        if (emailSet == null) {
            return new RankAndScore(0, 0); // 발생하지 않음
        }
        int myRank = getMyRank(emailSet);

        return new RankAndScore(myRank, myTotalScore.intValue());
    }

    private int getMyRank(Set<String> emailSet) {
        int myRank = -1;
        // emailSet의 크기는 항상 1일 것이다.
        for (String email : emailSet) {
            Long rank = zSetOperations.reverseRank(redisSetKey, email);
            if (rank == null) {
                continue; // 발생하지 않음
            }
            myRank = rank.intValue();
        }
        return myRank + 1;
    }
}
