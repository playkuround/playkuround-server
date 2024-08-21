package com.playkuround.playkuroundserver.domain.score.api;

import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
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
import static org.assertj.core.api.Assertions.tuple;
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
        landmarkRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("랭킹 유저가 한명도 없을 때")
    void getRankTop100ByLandmark_1() throws Exception {
        // given
        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmarkRepository.save(landmark);

        // expected
        mockMvc.perform(get("/api/scores/rank/{landmarkId}", landmark.getId()))
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
        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmarkRepository.save(landmark);

        for (int i = 1; i <= 50; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);

            Adventure adventure = new Adventure(user, landmark, ScoreType.CATCH, (long) i);
            adventureRepository.save(adventure);
        }

        // when
        MvcResult mvcResult =
                mockMvc.perform(get("/api/scores/rank/{landmarkId}", landmark.getId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andDo(print())
                        .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(50);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        for (int i = 0; i < 50; i++) {
            assertThat(rank.get(i).getNickname()).isEqualTo("user" + (50 - i));
            assertThat(rank.get(i).getScore()).isEqualTo(50 - i);
        }
        assertThat(response.getMyRank().getScore()).isZero();
        assertThat(response.getMyRank().getRanking()).isZero();
    }

    @Test
    @WithMockCustomUser(email = "test@konkuk.ac.kr")
    @DisplayName("전체 유저 100명 미만 + 내 랭킹 존재")
    void getRankTop100ByLandmark_3() throws Exception {
        // given
        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmarkRepository.save(landmark);

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
        MvcResult mvcResult =
                mockMvc.perform(get("/api/scores/rank/{landmarkId}", landmark.getId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andDo(print())
                        .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(51);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        for (int i = 0; i < 51; i++) {
            if (i < 14) {
                assertThat(rank.get(i).getNickname()).isEqualTo("user" + (50 - i));
                assertThat(rank.get(i).getScore()).isEqualTo(50 - i);
            }
            else if (i == 14) {
                assertThat(rank.get(i).getNickname()).isEqualTo("tester");
                assertThat(rank.get(i).getScore()).isEqualTo(37);
            }
            else {
                assertThat(rank.get(i).getNickname()).isEqualTo("user" + (51 - i));
                assertThat(rank.get(i).getScore()).isEqualTo(51 - i);
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
        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmarkRepository.save(landmark);

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
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank/{landmarkId}", landmark.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(100);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        for (int i = 0; i < 100; i++) {
            if (i < 40) {
                assertThat(rank.get(i).getNickname()).isEqualTo("user" + (101 - i));
                assertThat(rank.get(i).getScore()).isEqualTo(101 - i);
            }
            else if (i == 40) {
                assertThat(rank.get(i).getNickname()).isEqualTo("tester");
                assertThat(rank.get(i).getScore()).isEqualTo(62);
            }
            else {
                assertThat(rank.get(i).getNickname()).isEqualTo("user" + (102 - i));
                assertThat(rank.get(i).getScore()).isEqualTo(102 - i);
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
        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmarkRepository.save(landmark);

        for (int i = 1; i <= 101; i++) {
            User user = TestUtil.createUser("user" + i + "@konkuk.ac.kr", "user" + i, Major.건축학부);
            userRepository.save(user);

            adventureRepository.save(new Adventure(user, landmark, ScoreType.CATCH, (long) i));
            adventureRepository.save(new Adventure(user, landmark, ScoreType.BOOK, 1L));
        }

        User me = userRepository.findByEmail("test@konkuk.ac.kr").get();
        adventureRepository.save(new Adventure(me, landmark, ScoreType.CATCH, 62L));

        // when
        MvcResult mvcResult = mockMvc.perform(get("/api/scores/rank/{landmarkId}", landmark.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(100);
        List<ScoreRankingResponse.RankList> rank = response.getRank();
        for (int i = 0; i < 100; i++) {
            if (i < 41) {
                assertThat(rank.get(i).getNickname()).isEqualTo("user" + (101 - i));
                assertThat(rank.get(i).getScore()).isEqualTo(102 - i);
            }
            else if (i == 41) {
                assertThat(rank.get(i).getNickname()).isEqualTo("tester");
                assertThat(rank.get(i).getScore()).isEqualTo(62);
            }
            else {
                assertThat(rank.get(i).getNickname()).isEqualTo("user" + (102 - i));
                assertThat(rank.get(i).getScore()).isEqualTo(103 - i);
            }
        }
        assertThat(response.getMyRank().getScore()).isEqualTo(62);
        assertThat(response.getMyRank().getRanking()).isEqualTo(41); // 공동등수
    }

    @Test
    @WithMockCustomUser(email = "test@konkuk.ac.kr", badgeType = BadgeType.COLLEGE_OF_ENGINEERING)
    @DisplayName("랭킹 조회 API에는 사용자 대표 뱃지 데이터가 포함되어 있다.")
    void getRankTop100ByLandmark_6() throws Exception {
        // given
        Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
        landmarkRepository.save(landmark);
        {
            User me = userRepository.findByEmail("test@konkuk.ac.kr").get();
            adventureRepository.save(new Adventure(me, landmark, ScoreType.CATCH, 10L));
        }
        {
            User user1 = TestUtil.createUser("user1@konkuk.ac.kr", "user1", Major.건축학부);
            user1.updateProfileBadge(BadgeType.ATTENDANCE_1);
            userRepository.save(user1);
            adventureRepository.save(new Adventure(user1, landmark, ScoreType.CATCH, 5L));
        }
        {
            User user2 = TestUtil.createUser("user2@konkuk.ac.kr", "user2", Major.컴퓨터공학부);
            user2.updateProfileBadge(BadgeType.MONTHLY_RANKING_1);
            userRepository.save(user2);
            adventureRepository.save(new Adventure(user2, landmark, ScoreType.CATCH, 15L));
        }

        // when
        MvcResult mvcResult =
                mockMvc.perform(get("/api/scores/rank/{landmarkId}", landmark.getId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andDo(print())
                        .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ScoreRankingResponse response = TestUtil.convertFromJsonStringToObject(json, ScoreRankingResponse.class);

        // then
        assertThat(response.getRank()).hasSize(3)
                .extracting("nickname", "profileBadge", "score")
                .containsExactly(
                        tuple("user2", BadgeType.MONTHLY_RANKING_1.name(), 15),
                        tuple("tester", BadgeType.COLLEGE_OF_ENGINEERING.name(), 10),
                        tuple("user1", BadgeType.ATTENDANCE_1.name(), 5)
                );
        assertThat(response.getMyRank().getScore()).isEqualTo(10);
        assertThat(response.getMyRank().getRanking()).isEqualTo(2);
        assertThat(response.getMyRank().getProfileBadge()).isEqualTo(BadgeType.COLLEGE_OF_ENGINEERING.name());
    }
}
