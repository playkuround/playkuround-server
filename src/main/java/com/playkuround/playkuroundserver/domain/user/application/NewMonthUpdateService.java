package com.playkuround.playkuroundserver.domain.user.application;

import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.min;

@Service
@RequiredArgsConstructor
public class NewMonthUpdateService {

    @Value("${redis-key}")
    private String redisSetKey;

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final LandmarkRepository landmarkRepository;

    @Transactional
    public void updateMonth() {
        List<RankData> totalRankDateList = getTotalRankDateList();
        saveBadge(totalRankDateList);
        updateUserHighestRank(totalRankDateList);
        updateLandmarkRank();
        deleteAllTotalScoreRepository();
    }

    private List<RankData> getTotalRankDateList() {
        Set<ZSetOperations.TypedTuple<String>> totalScoreTypedTuples = getTotalScoreTypedTuples();

        List<RankData> rankDataList = new ArrayList<>();
        long rank = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : totalScoreTypedTuples) {
            if (typedTuple.getValue() == null || typedTuple.getScore() == null) {
                throw new RuntimeException("typedTuple value or score is null");
            }
            rankDataList.add(new RankData(typedTuple.getValue(), rank, typedTuple.getScore().longValue()));
            rank++;
        }
        return rankDataList;
    }

    private Set<ZSetOperations.TypedTuple<String>> getTotalScoreTypedTuples() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Long zSetSize = zSetOperations.size(redisSetKey);
        if (zSetSize == null) {
            throw new RuntimeException("zSetSize is null");
        }

        Set<ZSetOperations.TypedTuple<String>> typedTuples
                = zSetOperations.reverseRangeWithScores(redisSetKey, 0, zSetSize - 1);
        if (typedTuples == null) {
            throw new RuntimeException("typedTuples is null");
        }
        return typedTuples;
    }

    private void saveBadge(List<RankData> rankDataList) {
        BadgeType[] monthlyRankingBadges = {BadgeType.MONTHLY_RANKING_1, BadgeType.MONTHLY_RANKING_2, BadgeType.MONTHLY_RANKING_3};
        int maxIter = min(monthlyRankingBadges.length, rankDataList.size());

        for (int i = 0; i < maxIter; i++) {
            BadgeType badgeType = monthlyRankingBadges[i];
            RankData rankData = rankDataList.get(i);

            User user = userRepository.findByEmail(rankData.email)
                    .orElseThrow(UserNotFoundException::new);
            if (!badgeRepository.existsByUserAndBadgeType(user, badgeType)) {
                Badge badge = Badge.createBadge(user, badgeType);
                badgeRepository.save(badge);
                user.addNewBadgeNotification(badgeType);
            }
        }
    }

    private void updateUserHighestRank(List<RankData> rankDataList) {
        List<String> emailList = rankDataList.stream()
                .map(RankData::email)
                .toList();
        List<User> users = userRepository.findByEmailIn(emailList);
        Map<String, User> mapEmailToUser = users.stream()
                .collect(Collectors.toMap(User::getEmail, user -> user));

        for (RankData rankData : rankDataList) {
            User user = mapEmailToUser.get(rankData.email);
            if (user == null) {
                throw new UserNotFoundException();
            }
            user.updateHighestRank(rankData.rank, rankData.score);
        }
    }

    private void updateLandmarkRank() {
        landmarkRepository.findAll()
                .forEach(Landmark::deleteRank);
    }

    private void deleteAllTotalScoreRepository() {
        redisTemplate.delete(redisSetKey);
    }

    private record RankData(String email, long rank, long score) {
    }
}
