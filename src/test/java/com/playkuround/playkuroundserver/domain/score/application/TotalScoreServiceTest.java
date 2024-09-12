package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.score.api.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
class TotalScoreServiceTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TotalScoreService totalScoreService;

    @Autowired
    private AdventureRepository adventureRepository;

    @Value("${redis-key}")
    private String redisSetKey;

    @AfterEach
    void tearDown() {
        redisTemplate.delete(redisSetKey);
        adventureRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("유저 점수 저장하기")
    void incrementTotalScore() {
        User user = TestUtil.createUser();
        Random random = new Random();
        long ansSum = 0;
        for (int i = 0; i < 10; i++) {
            long score = random.nextInt(1000);
            ansSum += score;

            Long currentUserScore = totalScoreService.incrementTotalScore(user, score);
            assertThat(currentUserScore).isEqualTo(ansSum);
        }
    }

    @Test
    @DisplayName("탑 100명 랭킹 조회하기 : 랭킹 유저가 한명도 없을 때")
    void getRankTop100_1() {
        // expect
        User user = TestUtil.createUser();
        ScoreRankingResponse result = totalScoreService.getRankTop100(user);

        assertThat(result.getRank()).isEmpty();
        assertThat(result.getMyRank().getScore()).isZero();
        assertThat(result.getMyRank().getRanking()).isZero();
    }

    @Test
    @DisplayName("탑 100명 랭킹 조회하기 : 전체 유저 100명 미만 + 내 랭킹은 없음")
    void getRankTop100_2() {
        // given
        for (int i = 1; i <= 50; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);
            totalScoreService.incrementTotalScore(user, (long) i);
        }

        // when
        ScoreRankingResponse result = totalScoreService.getRankTop100(TestUtil.createUser());

        // then
        assertThat(result.getRank()).hasSize(50);
        List<ScoreRankingResponse.RankList> rank = result.getRank();
        for (int i = 0; i < 50; i++) {
            assertThat(rank.get(i).getNickname()).isEqualTo("user" + (50 - i));
            assertThat(rank.get(i).getScore()).isEqualTo(50 - i);
        }
        assertThat(result.getMyRank().getScore()).isZero();
        assertThat(result.getMyRank().getRanking()).isZero();
    }

    @Test
    @DisplayName("탑 100명 랭킹 조회하기 : 전체 유저 100명 미만 + 내 랭킹 존재")
    void getRankTop100_3() {
        // given
        for (int i = 1; i <= 50; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);
            totalScoreService.incrementTotalScore(user, (long) i);
        }
        User user = TestUtil.createUser();
        userRepository.save(user);
        totalScoreService.incrementTotalScore(user, (long) 13);

        // when
        ScoreRankingResponse result = totalScoreService.getRankTop100(user);

        // then
        assertThat(result.getRank()).hasSize(51);
        List<ScoreRankingResponse.RankList> rank = result.getRank();
        for (int i = 0; i < 51; i++) {
            if (i < 38) {
                assertThat(rank.get(i).getNickname()).isEqualTo("user" + (50 - i));
                assertThat(rank.get(i).getScore()).isEqualTo((50 - i));
            }
            else if (i == 38) {
                assertThat(rank.get(i).getNickname()).isEqualTo(user.getNickname());
                assertThat(rank.get(i).getScore()).isEqualTo(13);
            }
            else {
                assertThat(rank.get(i).getNickname()).isEqualTo("user" + (51 - i));
                assertThat(rank.get(i).getScore()).isEqualTo(51 - i);
            }
        }
        assertThat(result.getMyRank().getScore()).isEqualTo(13);
        assertThat(result.getMyRank().getRanking()).isEqualTo(38);
    }

    @Test
    @DisplayName("탑 100명 랭킹 조회하기 : 전체 유저 100명 초과 + 내 랭킹 공동 50위")
    void getRankTop100_4() {
        // given
        for (int i = 1; i <= 100; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);
            totalScoreService.incrementTotalScore(user, (long) i);
        }
        User user = TestUtil.createUser();
        userRepository.save(user);
        totalScoreService.incrementTotalScore(user, (long) 51);

        // when
        ScoreRankingResponse result = totalScoreService.getRankTop100(user);

        // then
        assertThat(result.getRank()).hasSize(100);
        List<ScoreRankingResponse.RankList> rank = result.getRank();
        assertThat(rank.get(0).getNickname()).isEqualTo("user100");
        assertThat(rank.get(0).getScore()).isEqualTo(100);
        assertThat(rank.get(99).getNickname()).isEqualTo("user2");
        assertThat(rank.get(99).getScore()).isEqualTo(2);

        assertThat(result.getMyRank().getScore()).isEqualTo(51);
        assertThat(result.getMyRank().getRanking()).isEqualTo(50);
    }

    @Test
    @DisplayName("탑 100명 랭킹 조회하기 : 전체 유저 100명 초과 + 내 랭킹 공동 91위")
    void getRankTop100_5() {
        // given
        for (int i = 1; i <= 90; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);
            totalScoreService.incrementTotalScore(user, (long) 1000 - i);
        }
        User me = null;
        for (int i = 91; i <= 105; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);
            totalScoreService.incrementTotalScore(user, 110L);
            if (i == 100) me = user;
        }

        // when
        ScoreRankingResponse result = totalScoreService.getRankTop100(me);

        // then
        assertThat(result.getRank()).hasSize(100);
        List<ScoreRankingResponse.RankList> rank = result.getRank();
        assertThat(rank.get(0).getNickname()).isEqualTo("user1");
        assertThat(rank.get(0).getScore()).isEqualTo(999);
        assertThat(rank.get(99).getNickname()).isEqualTo("user105");
        assertThat(rank.get(99).getScore()).isEqualTo(110);

        assertThat(result.getMyRank().getScore()).isEqualTo(110);
        assertThat(result.getMyRank().getRanking()).isEqualTo(91);
    }

    @Test
    @DisplayName("전체 랭킹 조회 결과에는 사용자 프로필 배지 데이터가 포함되어 있다.")
    void getRankTop100_6() {
        // given
        User user1 = TestUtil.createUser("user1@konkuk.ac.kr", "user1", Major.경영학과);
        User user2 = TestUtil.createUser("user2@konkuk.ac.kr", "user2", Major.건축학부);
        User user3 = TestUtil.createUser("user3@konkuk.ac.kr", "user3", Major.컴퓨터공학부);

        user1.updateProfileBadge(BadgeType.ATTENDANCE_CHRISTMAS_DAY);
        user2.updateProfileBadge(BadgeType.MONTHLY_RANKING_1);
        user3.updateProfileBadge(BadgeType.ATTENDANCE_1);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        totalScoreService.incrementTotalScore(user1, 15L);
        totalScoreService.incrementTotalScore(user2, 5L);
        totalScoreService.incrementTotalScore(user3, 10L);

        // when
        ScoreRankingResponse result = totalScoreService.getRankTop100(user1);

        // then
        assertThat(result.getRank()).hasSize(3)
                .extracting("nickname", "profileBadge", "score")
                .containsExactly(
                        tuple(user1.getNickname(), user1.getProfileBadge().name(), 15),
                        tuple(user3.getNickname(), user3.getProfileBadge().name(), 10),
                        tuple(user2.getNickname(), user2.getProfileBadge().name(), 5)

                );
        assertThat(result.getMyRank().getScore()).isEqualTo(15);
        assertThat(result.getMyRank().getRanking()).isEqualTo(1);
        assertThat(result.getMyRank().getProfileBadge()).isEqualTo(user1.getProfileBadge().name());
    }

}