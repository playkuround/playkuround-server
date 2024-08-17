package com.playkuround.playkuroundserver.domain.score.api;

import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.score.api.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationControllerTest
class LandmarkScoreRankApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdventureRepository adventureRepository;

    @Autowired
    private LandmarkRepository landmarkRepository;

    @AfterEach
    void clean() {
        adventureRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("랭킹 유저가 한명도 없을 때")
    void getRankTop100ByLandmark_1() throws Exception {
        // expected
        mockMvc.perform(get("/api/scores/rank/{landmarkId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.rank").isEmpty())
                .andExpect(jsonPath("$.response.myRank.score").value(0))
                .andExpect(jsonPath("$.response.myRank.ranking").value(0))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("전체 유저 100명 미만 + 내 랭킹은 없음")
    void getRankTop100ByLandmark_2() throws Exception {
        // given
        Landmark landmark = landmarkRepository.findById(1L).get();
        for (int i = 1; i <= 50; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);

            Adventure adventure = new Adventure(user, landmark, ScoreType.CATCH, (long) i);
            adventureRepository.save(adventure);
        }

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank/{landmarkId}", 1))
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
        assertThat(response.getMyRank().getScore()).isZero();
        assertThat(response.getMyRank().getRanking()).isZero();
    }

    @Test
    @WithMockCustomUser(email = "test@konkuk.ac.kr")
    @DisplayName("전체 유저 100명 미만 + 내 랭킹 존재")
    void getRankTop100ByLandmark_3() throws Exception {
        // given
        Landmark landmark = landmarkRepository.findById(1L).get();
        for (int i = 1; i <= 50; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);

            Adventure adventure = new Adventure(user, landmark, ScoreType.CATCH, (long) i);
            adventureRepository.save(adventure);
        }
        User me = userRepository.findByEmail("test@konkuk.ac.kr").get();
        Adventure adventure = new Adventure(me, landmark, ScoreType.CATCH, 37L);
        adventureRepository.save(adventure);

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank/{landmarkId}", 1))
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
            if (i < 15) {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("user" + (51 - i));
                assertThat(rank.get(i - 1).getScore()).isEqualTo(51 - i);
            }
            else if (i == 15) {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("tester");
                assertThat(rank.get(i - 1).getScore()).isEqualTo(37);
            }
            else {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("user" + (52 - i));
                assertThat(rank.get(i - 1).getScore()).isEqualTo(52 - i);
            }
        }
        assertThat(response.getMyRank().getScore()).isEqualTo(37);
        assertThat(response.getMyRank().getRanking()).isEqualTo(14);
    }

    @Test
    @WithMockCustomUser(email = "test@konkuk.ac.kr")
    @DisplayName("전체 유저 100명 초과 + 내 랭킹 중간에 존재")
    void getRankTop100ByLandmark_4() throws Exception {
        // given
        Landmark landmark = landmarkRepository.findById(1L).get();
        for (int i = 1; i <= 101; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);

            Adventure adventure = new Adventure(user, landmark, ScoreType.CATCH, (long) i);
            adventureRepository.save(adventure);
        }
        User me = userRepository.findByEmail("test@konkuk.ac.kr").get();
        Adventure adventure = new Adventure(me, landmark, ScoreType.CATCH, 62L);
        adventureRepository.save(adventure);

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank/{landmarkId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(100);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        for (int i = 1; i <= 100; i++) {
            if (i < 41) {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("user" + (102 - i));
                assertThat(rank.get(i - 1).getScore()).isEqualTo(102 - i);
            }
            else if (i == 41) { // 랭킹은 점수, 닉네임 내림차순
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("tester");
                assertThat(rank.get(i - 1).getScore()).isEqualTo(62);
            }
            else {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("user" + (103 - i));
                assertThat(rank.get(i - 1).getScore()).isEqualTo(103 - i);
            }
        }
        assertThat(response.getMyRank().getScore()).isEqualTo(62);
        assertThat(response.getMyRank().getRanking()).isEqualTo(40); // 공동등수
    }

    @Test
    @WithMockCustomUser(email = "test@konkuk.ac.kr")
    @DisplayName("점수는 SUM으로 계산된 결과로 비교된다")
    void getRankTop100ByLandmark_5() throws Exception {
        // given
        Landmark landmark = landmarkRepository.findById(1L).get();
        for (int i = 1; i <= 101; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);

            Adventure adventure = new Adventure(user, landmark, ScoreType.CATCH, (long) i);
            adventureRepository.save(adventure);
            adventure = new Adventure(user, landmark, ScoreType.BOOK, 1L);
            adventureRepository.save(adventure);
        }
        User me = userRepository.findByEmail("test@konkuk.ac.kr").get();
        Adventure adventure = new Adventure(me, landmark, ScoreType.CATCH, 62L);
        adventureRepository.save(adventure);

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank/{landmarkId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(100);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        for (int i = 1; i <= 100; i++) {
            if (i < 42) {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("user" + (102 - i));
                assertThat(rank.get(i - 1).getScore()).isEqualTo(103 - i);
            }
            else if (i == 42) { // 랭킹은 점수, 닉네임 내림차순
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("tester");
                assertThat(rank.get(i - 1).getScore()).isEqualTo(62);
            }
            else {
                assertThat(rank.get(i - 1).getNickname()).isEqualTo("user" + (103 - i));
                assertThat(rank.get(i - 1).getScore()).isEqualTo(104 - i);
            }
        }
        assertThat(response.getMyRank().getScore()).isEqualTo(62);
        assertThat(response.getMyRank().getRanking()).isEqualTo(41); // 공동등수
    }
}
