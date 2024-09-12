package com.playkuround.playkuroundserver.domain.landmark.api;

import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationControllerTest
class LandmarkApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LandmarkRepository landmarkRepository;

    @AfterEach
    void clear() {
        landmarkRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Nested
    @WithMockCustomUser
    @DisplayName("가장 가까운 랜드마크 찾기")
    class findNearestLandmark {

        @Test
        @DisplayName("위치 완전 동일")
        void success_1() throws Exception {
            // given
            Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
            landmarkRepository.save(landmark);

            // expected
            mockMvc.perform(get("/api/landmarks")
                            .param("latitude", String.valueOf(landmark.getLatitude()))
                            .param("longitude", String.valueOf(landmark.getLongitude())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.response.landmarkId").value(landmark.getId()))
                    .andExpect(jsonPath("$.response.name").value(landmark.getName().name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("허용 범위 내에서 조회")
        void success_2() throws Exception {
            // given
            Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 1000);
            landmarkRepository.save(landmark);

            // expected
            mockMvc.perform(get("/api/landmarks")
                            .param("latitude", String.valueOf(landmark.getLatitude() + 0.0001))
                            .param("longitude", String.valueOf(landmark.getLongitude())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.response.landmarkId").value(landmark.getId()))
                    .andExpect(jsonPath("$.response.name").value(landmark.getName().name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("허용 범위 밖에서 조회")
        void success_3() throws Exception {
            // given
            Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
            landmarkRepository.save(landmark);

            // expected
            mockMvc.perform(get("/api/landmarks")
                            .param("latitude", "13")
                            .param("longitude", "13"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.response").isEmpty())
                    .andDo(print());
        }
    }

    @Nested
    @WithMockCustomUser
    @DisplayName("랜드마크 최고점 유저 찾기")
    class findHighestUserByLandmark {

        @Test
        @DisplayName("랜드마크에 최고점 유저가 있다면 반환한다")
        void success_1() throws Exception {
            // given
            User user = userRepository.findAll().get(0);
            user.updateProfileBadge(BadgeType.ATTENDANCE_50);
            userRepository.save(user);

            long score = 1000;
            Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
            landmark.updateFirstUser(user, score);
            landmarkRepository.save(landmark);

            // expected
            mockMvc.perform(get("/api/landmarks/{landmarkId}/highest", landmark.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.response.score").value(score))
                    .andExpect(jsonPath("$.response.nickname").value(user.getNickname()))
                    .andExpect(jsonPath("$.response.profileBadge").value(user.getProfileBadge().name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("랜드마크에 최고점 유저가 없다면 빈 응답을 반환한다")
        void success_2() throws Exception {
            // given
            Landmark landmark = new Landmark(LandmarkType.경영관, 37.541, 127.079, 100);
            landmarkRepository.save(landmark);

            // expected
            mockMvc.perform(get("/api/landmarks/{landmarkId}/highest", landmark.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.response").isEmpty())
                    .andDo(print());
        }

    }

}