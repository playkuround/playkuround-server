package com.playkuround.playkuroundserver.domain.adventure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.adventure.api.request.AdventureSaveRequest;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
@Sql(scripts = {"/data-mysql.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AdventureApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdventureRepository adventureRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private LandmarkRepository landmarkRepository;

    private final String redisSetKey = "ranking";

    @AfterEach
    void clean() {
        badgeRepository.deleteAll();
        adventureRepository.deleteAll();
        landmarkRepository.deleteAll();
        userRepository.deleteAll();
        redisTemplate.delete(redisSetKey);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("탐험 저장")
    void saveAdventure_1() throws Exception {
        // given
        Landmark landmark = landmarkRepository.findById(3L).get();

        AdventureSaveRequest adventureSaveRequest
                = new AdventureSaveRequest(landmark.getId(), landmark.getLatitude(), landmark.getLongitude(), 100L, ScoreType.BOOK.name());
        String request = objectMapper.writeValueAsString(adventureSaveRequest);

        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.newBadges.size()").value(1))
                .andExpect(jsonPath("$.response.newBadges[0].name").value(BadgeType.COLLEGE_OF_VETERINARY_MEDICINE.name()))
                .andExpect(jsonPath("$.response.newBadges[0].description").value(BadgeType.COLLEGE_OF_VETERINARY_MEDICINE.getDescription()))
                .andDo(print());

        // Total Score 저장 및 최고 점수 갱신
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        User user = users.get(0);
        assertThat(user.getHighestScore().getHighestCardScore()).isEqualTo(100L);

        // adventure 저장
        List<Adventure> adventures = adventureRepository.findAll();
        assertThat(adventures).hasSize(1);
        Adventure adventure = adventures.get(0);
        assertThat(adventure.getScore()).isEqualTo(100L);
        assertThat(adventure.getScoreType()).isEqualTo(ScoreType.BOOK);
        assertThat(adventure.getUser().getId()).isEqualTo(user.getId());
        assertThat(adventure.getLandmark().getId()).isEqualTo(landmark.getId());

        // 랜드마크 최고 점수 갱신
        Landmark updatedLandmark = landmarkRepository.findById(landmark.getId()).get();
        assertThat(updatedLandmark.getHighestScore()).isEqualTo(100L);
        assertThat(updatedLandmark.getFirstUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("탐험 저장 : 랜드마크가 존재하지 않으면 에러가 발생한다.")
    void saveAdventure_2() throws Exception {
        // given
        Landmark landmark = landmarkRepository.findById(3L).get();

        AdventureSaveRequest adventureSaveRequest
                = new AdventureSaveRequest(-1L, landmark.getLatitude(), landmark.getLongitude(), 100L, ScoreType.BOOK.name());
        String request = objectMapper.writeValueAsString(adventureSaveRequest);

        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_VALUE.getStatus().value()))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("탐험 저장 : 인식 거리 밖에 있으면 에러가 발생한다.")
    void saveAdventure_3() throws Exception {
        // given
        Landmark landmark = landmarkRepository.findById(3L).get();

        AdventureSaveRequest adventureSaveRequest
                = new AdventureSaveRequest(landmark.getId(), 0.0, 0.0, 100L, ScoreType.BOOK.name());
        String request = objectMapper.writeValueAsString(adventureSaveRequest);

        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.INVALID_LOCATION_LANDMARK.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.INVALID_LOCATION_LANDMARK.getMessage()))
                .andExpect(jsonPath("$.errorResponse.status").value(ErrorCode.INVALID_LOCATION_LANDMARK.getStatus().value()))
                .andDo(print());
    }
}