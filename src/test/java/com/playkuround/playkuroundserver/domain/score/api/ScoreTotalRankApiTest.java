package com.playkuround.playkuroundserver.domain.score.api;

import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationControllerTest
class ScoreTotalRankApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String redisSetKey = "ranking";

    @AfterEach
    void clean() {
        userRepository.deleteAllInBatch();
        redisTemplate.delete("ranking");
    }

    @Test
    @WithMockCustomUser
    @DisplayName("탑 100명 랭킹 조회하기 : 랭킹 유저가 한명도 없을 때")
    void getRankTop100_1() throws Exception {
        // expected
        mockMvc.perform(get("/api/scores/rank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.rank").isEmpty())
                .andExpect(jsonPath("$.response.myRank.score").value(0))
                .andExpect(jsonPath("$.response.myRank.ranking").value(0))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("탑 100명 랭킹 조회하기 : 전체 유저 100명 미만 + 내 랭킹은 없음")
    void getRankTop100_2() throws Exception {
        // given
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        for (int i = 1; i <= 50; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);
            zSetOperations.incrementScore(redisSetKey, user.getEmail(), i);
        }

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(50);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        for (int i = 1; i <= 50; i++) {
            assertThat(rank.get(i - 1).getNickname()).isEqualTo("user" + (51 - i));
            assertThat(rank.get(i - 1).getScore()).isEqualTo(51 - i);
        }
        assertThat(response.getMyRank().getScore()).isEqualTo(0);
        assertThat(response.getMyRank().getRanking()).isEqualTo(0);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("탑 100명 랭킹 조회하기 : 전체 유저 100명 미만 + 내 랭킹 존재")
    void getRankTop100_3() throws Exception {
        // given
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        for (int i = 1; i <= 50; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);
            zSetOperations.incrementScore(redisSetKey, user.getEmail(), i);
        }
        User user = TestUtil.createUser();
        zSetOperations.incrementScore(redisSetKey, user.getEmail(), 13);

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(51);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        for (int i = 1; i <= 51; i++) {
            if (i < 39) {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("user" + (51 - i));
                assertThat(rank.get(i - 1).getScore()).isEqualTo((51 - i));
            }
            else if (i == 39) {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo(user.getNickname());
                assertThat(rank.get(i - 1).getScore()).isEqualTo(13);
            }
            else {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("user" + (52 - i));
                assertThat(rank.get(i - 1).getScore()).isEqualTo(52 - i);
            }
        }
        assertThat(response.getMyRank().getScore()).isEqualTo(13);
        assertThat(response.getMyRank().getRanking()).isEqualTo(38);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("탑 100명 랭킹 조회하기 : 전체 유저 100명 초과 + 내 랭킹 공동 50위")
    void getRankTop100_4() throws Exception {
        // given
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        for (int i = 1; i <= 100; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);
            zSetOperations.incrementScore(redisSetKey, user.getEmail(), i);
        }
        User user = TestUtil.createUser();
        zSetOperations.incrementScore(redisSetKey, user.getEmail(), 51);

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(100);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        assertThat(rank.get(0).getNickname()).isEqualTo("user100");
        assertThat(rank.get(0).getScore()).isEqualTo(100);
        assertThat(rank.get(99).getNickname()).isEqualTo("user2");
        assertThat(rank.get(99).getScore()).isEqualTo(2);

        assertThat(response.getMyRank().getScore()).isEqualTo(51);
        assertThat(response.getMyRank().getRanking()).isEqualTo(50);
    }

    @Test
    @WithMockCustomUser(email = "user100@konkuk.ac.kr")
    @DisplayName("탑 100명 랭킹 조회하기 : 전체 유저 100명 초과 + 내 랭킹 공동 91위")
    void getRankTop100_5() throws Exception {
        // given
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        for (int i = 1; i <= 90; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);
            zSetOperations.incrementScore(redisSetKey, user.getEmail(), 1000 - i);
        }
        for (int i = 91; i <= 105; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            if (i != 100) userRepository.save(user);
            zSetOperations.incrementScore(redisSetKey, user.getEmail(), 110);
        }

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(100);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        assertThat(rank.get(0).getNickname()).isEqualTo("user1");
        assertThat(rank.get(0).getScore()).isEqualTo(999);
        assertThat(rank.get(99).getNickname()).isEqualTo("user105");
        assertThat(rank.get(99).getScore()).isEqualTo(110);

        assertThat(response.getMyRank().getScore()).isEqualTo(110);
        assertThat(response.getMyRank().getRanking()).isEqualTo(91);
    }
}