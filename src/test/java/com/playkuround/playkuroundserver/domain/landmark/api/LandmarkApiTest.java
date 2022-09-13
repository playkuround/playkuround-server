package com.playkuround.playkuroundserver.domain.landmark.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.landmark.dto.FindNearestLandmark;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class LandmarkApiTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("현재 위치에서 가장 가까운 landmark 조회1 - 위치 완전 동일")
    void findNearestLandmark1() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        FindNearestLandmark.Request request = new FindNearestLandmark.Request(37.5424445, 127.0779958);
        String content = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(get("/api/landmarks/nearest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.id").value(19))
                .andExpect(jsonPath("$.response.name").value("인문학관(인문대)"))
                .andExpect(jsonPath("$.response.distance").value(0))
                .andExpect(jsonPath("$.response.gameType").value("QUIZ"))
                .andDo(print());
    }

    @Test
    @DisplayName("현재 위치에서 가장 가까운 landmark 조회2 - 아주 약간 옆에서 조회")
    void findNearestLandmark2() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        FindNearestLandmark.Request request = new FindNearestLandmark.Request(37.5424444, 127.077995);
        String content = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(get("/api/landmarks/nearest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.id").value(19))
                .andExpect(jsonPath("$.response.name").value("인문학관(인문대)"))
                .andExpect(jsonPath("$.response.distance").value(0))
                .andExpect(jsonPath("$.response.gameType").value("QUIZ"))
                .andDo(print());
    }
}