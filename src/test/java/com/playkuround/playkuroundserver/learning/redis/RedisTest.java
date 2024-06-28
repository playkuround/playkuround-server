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
public class RedisTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.delete("ranking");
    }

    @Test
    void 점수_오름차순_정렬() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        final String key = "ranking";
        zSetOperations.add(key, "a", 1);
        zSetOperations.add(key, "c", 2);
        zSetOperations.add(key, "b", 3);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(key, 0, 9);
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
        final String key = "ranking";
        zSetOperations.add(key, "a", 1);
        zSetOperations.add(key, "c", 2);
        zSetOperations.add(key, "b", 3);

        zSetOperations.incrementScore(key, "a", 10);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(key, 0, 9);
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
        final String key = "ranking";
        zSetOperations.incrementScore(key, "a", 10);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(key, 0, 9);
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
        final String key = "ranking";
        zSetOperations.add(key, "a", 1);
        zSetOperations.add(key, "c", 2);
        zSetOperations.add(key, "b", 3);

        zSetOperations.add(key, "a", 10);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(key, 0, 9);
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
        final String key = "ranking";
        zSetOperations.add(key, "a", 1);
        zSetOperations.add(key, "c", 1);
        zSetOperations.add(key, "b", 1);

        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(key, 0, 9);
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
        final String key = "ranking";
        zSetOperations.add(key, "a", 1);
        zSetOperations.add(key, "c", 3);
        zSetOperations.add(key, "me", 2);
        zSetOperations.add(key, "b", 4);

        Double myTotalScore = zSetOperations.score(key, "me");
        assertThat(myTotalScore).isEqualTo(2.0);

        Set<String> values = zSetOperations.reverseRangeByScore(key, myTotalScore, myTotalScore, 0, 1);
        assertThat(values.size()).isEqualTo(1);
        for (String value : values) {
            Long myRank = zSetOperations.reverseRank(key, value);
            assertThat(myRank).isEqualTo(2L); // 3등
            System.out.println("나의 등수는 " + myRank + 1 + "입니다.");
        }
    }

    @Test
    void 동점자가_존재할_때_내_등수() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        final String key = "ranking";
        zSetOperations.add(key, "z", 1);
        zSetOperations.add(key, "c", 3);
        zSetOperations.add(key, "me", 1);
        zSetOperations.add(key, "b", 4);

        Double myTotalScore = zSetOperations.score(key, "me");
        assertThat(myTotalScore).isEqualTo(1.0);

        Set<String> values = zSetOperations.reverseRangeByScore(key, myTotalScore, myTotalScore, 0, 1);
        assertThat(values.size()).isEqualTo(1); // 정렬 기준이 높은 사람만 1명 나온다.
        for (String value : values) {
            Long myRank = zSetOperations.reverseRank(key, value);
            assertThat(value).isEqualTo("z");
            assertThat(myRank).isEqualTo(2L); // 3등
            System.out.println("나의 등수는 " + (myRank + 1) + "입니다.");
        }
    }

    @Test
    void 여러명_존재할_때_테스트() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        final String key = "ranking";
        Random random = new Random();
        List<Pair<String, Double>> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            int randomInt = random.nextInt(50);
            double score = (double) randomInt;
            zSetOperations.add(key, "user" + i, score);
            list.add(Pair.of("user" + i, score));
        }

        list.sort((o1, o2) -> {
            if (o1.getSecond().equals(o2.getSecond())) return o2.getFirst().compareTo(o1.getFirst());
            else return o1.getSecond() > o2.getSecond() ? -1 : 1;
        });
        list.forEach(System.out::println);

        // 전체 랭킹
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSetOperations.reverseRangeWithScores(key, 0, 99);
        int i = 0;
        assertThat(typedTuples.size()).isEqualTo(100);
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            String value = typedTuple.getValue();
            Double score = typedTuple.getScore();
            assertThat(value).isEqualTo(list.get(i).getFirst());
            assertThat(score).isEqualTo(list.get(i).getSecond());
            i++;
        }

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

            Double myScore = zSetOperations.score(key, user);
            Set<String> values = zSetOperations.reverseRangeByScore(key, myScore, myScore, 0, 1);
            assertThat(myScore).isEqualTo(myRealScore);
            assertThat(values.size()).isEqualTo(1);
            for (String value : values) {
                Long myRank = zSetOperations.reverseRank(key, value);
                assertThat(value).isEqualTo(firstUser);
                assertThat(myRank).isEqualTo(myRealRank);
            }
        }
    }

    @Test
    void 저장안된_value이면_null이_반환된다() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        final String key = "ranking";
        Double myScore = zSetOperations.score(key, "notSavedUser");
        assertThat(myScore).isNull();
    }
}
