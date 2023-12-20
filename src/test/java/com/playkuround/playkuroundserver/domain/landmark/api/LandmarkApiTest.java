package com.playkuround.playkuroundserver.domain.landmark.api;

import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LandmarkApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("주변 landmark 조회1 - 위치 완전 동일")
    void findNearestLandmark1() throws Exception {
        // expected
        mockMvc.perform(get("/api/landmarks")
                        .param("latitude", "37.542602")
                        .param("longitude", "127.078250")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.landmarkId").value(19))
                .andExpect(jsonPath("$.response.name").value("인문학관(인문대)"))
                .andExpect(jsonPath("$.response.distance").value(0.0))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("주변 landmark 조회2 - 허용 범위 내에서 조회")
    void findNearestLandmark2() throws Exception {
        // expected
        mockMvc.perform(get("/api/landmarks")
                        .param("latitude", "37.542600")
                        .param("longitude", "127.078200")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.landmarkId").value(19))
                .andExpect(jsonPath("$.response.name").value("인문학관(인문대)"))
                .andExpect(jsonPath("$.response.distance").value(4))
                .andDo(print());
    }

    @Test
    @WithMockCustomUser
    @DisplayName("주변 landmark 조회3 - 허용 범위 밖에서 조회")
    void findNearestLandmark3() throws Exception {
        // expected
        mockMvc.perform(get("/api/landmarks")
                        .param("latitude", "13")
                        .param("longitude", "13")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.length()").value(0))
                .andDo(print());
    }
}