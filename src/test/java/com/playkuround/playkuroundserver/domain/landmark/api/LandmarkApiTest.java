package com.playkuround.playkuroundserver.domain.landmark.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.adventure.application.AdventureService;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.RequestSaveAdventure;
import com.playkuround.playkuroundserver.domain.landmark.dto.FindNearestLandmark;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc //MockMvc 사용
@SpringBootTest
@ActiveProfiles("test")
class LandmarkApiTest {

    @Autowired
    private ObjectMapper objectMapper; // 스프링에서 자동으로 주입해줌

    @Autowired
    private AdventureService adventureService;

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdventureRepository adventureRepository;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clean() {
        adventureRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("현재 위치에서 가장 가까운 landmark 조회")
    void findNearestLandmark() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        FindNearestLandmark.Request request = new FindNearestLandmark.Request(37.5424445, 127.0779958);
        String content = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/api/landmarks/nearest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(19))
                .andExpect(jsonPath("$.name").value("인문학관(인문대)"))
                .andExpect(jsonPath("$.distance").value(0))
                .andExpect(jsonPath("$.gameType").value("QUIZ"))
                .andDo(print());

    }
}