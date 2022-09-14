package com.playkuround.playkuroundserver.domain.landmark.api;

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
    @DisplayName("주변 landmark 조회1 - 위치 완전 동일")
    void findNearestLandmark1() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        // expected
        mockMvc.perform(get("/api/landmarks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("latitude", "37.5424445")
                        .param("longitude", "127.0779958")
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
    @DisplayName("주변 landmark 조회2 - 아주 약간 옆에서 조회")
    void findNearestLandmark2() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        // expected
        mockMvc.perform(get("/api/landmarks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("latitude", "37.5424444")
                        .param("longitude", "127.077995")
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
    @DisplayName("주변 landmark 조회3 - 건대 밖에서 조회")
    void findNearestLandmark3() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        // expected
        mockMvc.perform(get("/api/landmarks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("latitude", "13")
                        .param("longitude", "13")
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.length()").value(0))
                .andDo(print());
    }
}