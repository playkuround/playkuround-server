package com.playkuround.playkuroundserver.learning.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@DisplayName("Redis ZSet 테스트")
@SpringBootTest(properties = "spring.profiles.active=test")
class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    final String redisKey = "testKey";

    @AfterEach
    void tearDown() {
        redisTemplate.delete(redisKey);
    }

    @Test
    void 점수_오름차순_정렬() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(redisKey, "a", 1);
        zSetOperations.add(redisKey, "c", 2);
        zSetOperations.add(redisKey, "b", 3);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(redisKey, 0, 9);
        int i = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String value = typedTuple.getValue();
            Double score = typedTuple.getScore();
            System.out.println(value + " : " + score);

            if (i == 1) {
                assertThat(value).isEqualTo("b");
                assertThat(score).isEqualTo(3.0);
            }
            if (i == 2) {
                assertThat(value).isEqualTo("c");
                assertThat(score).isEqualTo(2.0);
            }
            if (i == 3) {
                assertThat(value).isEqualTo("a");
                assertThat(score).isEqualTo(1.0);
            }
            i++;
        }
    }

    @Test
    void 점수_증가시키기() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        zSetOperations.add(redisKey, "a", 1);
        zSetOperations.add(redisKey, "c", 2);
        zSetOperations.add(redisKey, "b", 3);
        zSetOperations.incrementScore(redisKey, "a", 10);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(redisKey, 0, 9);
        int i = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String value = typedTuple.getValue();
            Double score = typedTuple.getScore();
            System.out.println(value + " : " + score);

            if (i == 1) {
                assertThat(value).isEqualTo("a");
                assertThat(score).isEqualTo(11.0);
            }
            if (i == 2) {
                assertThat(value).isEqualTo("b");
                assertThat(score).isEqualTo(3.0);
            }
            if (i == 3) {
                assertThat(value).isEqualTo("c");
                assertThat(score).isEqualTo(2.0);
            }
            i++;
        }
    }

    @Test
    void value가_존재하지_않을때도_increment_수행하면_score가_0에서_증가() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.incrementScore(redisKey, "a", 10);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(redisKey, 0, 9);
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String value = typedTuple.getValue();
            Double score = typedTuple.getScore();
            System.out.println(value + " : " + score);
            assertThat(value).isEqualTo("a");
            assertThat(score).isEqualTo(10.0);
        }
    }

    @Test
    void 이미_존재하는_value를_add하면_덮어써진다() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(redisKey, "a", 1);
        zSetOperations.add(redisKey, "c", 2);
        zSetOperations.add(redisKey, "b", 3);
        zSetOperations.add(redisKey, "a", 10);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(redisKey, 0, 9);
        int i = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String value = typedTuple.getValue();
            Double score = typedTuple.getScore();
            System.out.println(value + " : " + score);

            if (i == 1) {
                assertThat(value).isEqualTo("a");
                assertThat(score).isEqualTo(10.0);
            }
            if (i == 2) {
                assertThat(value).isEqualTo("b");
                assertThat(score).isEqualTo(3.0);
            }
            if (i == 3) {
                assertThat(value).isEqualTo("c");
                assertThat(score).isEqualTo(2.0);
            }
            i++;
        }
    }

    @Test
    void 동점자가_존재하면_value_순으로_정렬된다() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(redisKey, "a", 1);
        zSetOperations.add(redisKey, "c", 1);
        zSetOperations.add(redisKey, "b", 1);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(redisKey, 0, 9);
        int i = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String value = typedTuple.getValue();
            Double score = typedTuple.getScore();
            System.out.println(value + " : " + score);

            if (i == 1) {
                assertThat(value).isEqualTo("c");
                assertThat(score).isEqualTo(1.0);
            }
            if (i == 2) {
                assertThat(value).isEqualTo("b");
                assertThat(score).isEqualTo(1.0);
            }
            if (i == 3) {
                assertThat(value).isEqualTo("a");
                assertThat(score).isEqualTo(1.0);
            }
            i++;
        }
    }

    @Test
    void 내_등수_얻기() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(redisKey, "a", 1);
        zSetOperations.add(redisKey, "c", 3);
        zSetOperations.add(redisKey, "me", 2);
        zSetOperations.add(redisKey, "b", 4);

        Double myTotalScore = zSetOperations.score(redisKey, "me");
        assertThat(myTotalScore).isEqualTo(2.0);

        Set<String> values = zSetOperations.reverseRangeByScore(redisKey, myTotalScore, myTotalScore, 0, 1);
        assertThat(values.size()).isEqualTo(1);
        for (String value : values) {
            Long myRank = zSetOperations.reverseRank(redisKey, value);
            assertThat(myRank).isEqualTo(2L); // 3등
            System.out.println("나의 등수는 " + myRank + 1 + "입니다.");
        }
    }

    @Test
    void 동점자가_존재할_때_내_등수() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(redisKey, "z", 1);
        zSetOperations.add(redisKey, "c", 3);
        zSetOperations.add(redisKey, "me", 1);
        zSetOperations.add(redisKey, "b", 4);

        Double myTotalScore = zSetOperations.score(redisKey, "me");
        assertThat(myTotalScore).isEqualTo(1.0);

        Set<String> values = zSetOperations.reverseRangeByScore(redisKey, myTotalScore, myTotalScore, 0, 1);
        assertThat(values.size()).isEqualTo(1); // 정렬 기준이 높은 사람만 1명 나온다.
        for (String value : values) {
            Long myRank = zSetOperations.reverseRank(redisKey, value);
            assertThat(value).isEqualTo("z");
            assertThat(myRank).isEqualTo(2L); // 3등
            System.out.println("나의 등수는 " + (myRank + 1) + "입니다.");
        }
    }

    @Test
    void 여러명_존재할_때_테스트() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Random random = new Random();
        List<Pair<String, Double>> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            double score = random.nextInt(50);
            zSetOperations.add(redisKey, "user" + i, score);
            list.add(Pair.of("user" + i, score));
        }

        list.sort((o1, o2) -> {
            if (o1.getSecond().equals(o2.getSecond())) return o2.getFirst().compareTo(o1.getFirst());
            else return o1.getSecond() > o2.getSecond() ? -1 : 1;
        });
        list.forEach(System.out::println);

        // 전체 랭킹
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(redisKey, 0, 99);
        assertThat(typedTuples).hasSize(100)
                .map(v -> Pair.of(v.getValue(), v.getScore()))
                .containsExactlyElementsOf(list);

        // 특정 유저의 등수
        for (int j = 1; j <= 50; j++) {
            int index = random.nextInt(100);
            String user = list.get(index).getFirst();

            Double myRealScore = list.get(index).getSecond();
            Long myRealRank = -1L;
            int duplicateCount = 0;
            String firstUser = "";
            for (int k = 0; k < 100; k++) {
                if (myRealScore.equals(list.get(k).getSecond())) {
                    if (myRealRank == -1L) {
                        myRealRank = (long) k;
                        firstUser = list.get(k).getFirst();
                    }
                    duplicateCount++;
                }
            }
            System.out.println("해당 점수를 가진 유저 수: " + duplicateCount + "명, 최우선순위(value내림차순): " + firstUser);

            Double myScore = zSetOperations.score(redisKey, user);
            Set<String> values = zSetOperations.reverseRangeByScore(redisKey, myScore, myScore, 0, 1);
            assertThat(myScore).isEqualTo(myRealScore);
            assertThat(values).hasSize(1);
            for (String value : values) {
                Long myRank = zSetOperations.reverseRank(redisKey, value);
                assertThat(value).isEqualTo(firstUser);
                assertThat(myRank).isEqualTo(myRealRank);
            }
        }
    }

    @Test
    void 저장안된_value이면_null이_반환된다() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Double myScore = zSetOperations.score(redisKey, "notSavedUser");
        assertThat(myScore).isNull();
    }
}
