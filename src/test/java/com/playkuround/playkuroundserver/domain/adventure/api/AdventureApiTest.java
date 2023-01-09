package com.playkuround.playkuroundserver.domain.adventure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.adventure.application.AdventureService;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc //MockMvc 사용
@SpringBootTest
@ActiveProfiles("test")
class AdventureApiTest {

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

    @AfterEach
    void clean() {
        adventureRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("탐험 저장 + 첫 탐험 배지(ADVENTURE_1) 획득")
    void saveAdventure() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(1L, 37.539927, 127.073006);
        String content = objectMapper.writeValueAsString(adventureSaveDto);

        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.newBadges[?(@.name == '%s')]", BadgeType.ADVENTURE_1.name()).exists())
                .andExpect(jsonPath("$.response.newBadges[?(@.description == '%s')]", BadgeType.ADVENTURE_1.getDescription()).exists())
                .andDo(print());

        Adventure adventure = adventureRepository.findAll().get(0);
        assertEquals(1L, adventureRepository.count());
        assertEquals(1L, adventure.getLandmark().getId());

        User user = userRepository.findAll().get(0);
        assertEquals(1L, userRepository.count());
        assertEquals(user.getId(), adventure.getUser().getId());
    }

    @Test
    @DisplayName("탐험 5번 배지(ADVENTURE_5) 획득")
    void adventureBadge_5() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(2L, 37.540158, 127.073463));
        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(3L, 37.539314, 127.074319));
        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(4L, 37.540099, 127.073976));
        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(5L, 37.541211, 127.073883));

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(1L, 37.539927, 127.073006);
        String content = objectMapper.writeValueAsString(adventureSaveDto);

        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.newBadges[?(@.name == '%s')]", BadgeType.ADVENTURE_5.name()).exists())
                .andExpect(jsonPath("$.response.newBadges[?(@.description == '%s')]", BadgeType.ADVENTURE_5.getDescription()).exists())
                .andDo(print());
    }

    @Test
    @DisplayName("탐험 5번 배지(ADVENTURE_5) + 공대 건물(ENGINEER) 배지 획득")
    void adventureBadge_5_ENGINEER() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(22L, 37.541755, 127.078681));
        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(23L, 37.542101, 127.079445));
        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(24L, 37.541135, 127.079324));
        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(25L, 37.540884, 127.079518));

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(26L, 37.541250, 127.080375);
        String content = objectMapper.writeValueAsString(adventureSaveDto);

        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.newBadges[?(@.name == '%s')]", BadgeType.ADVENTURE_5.name()).exists())
                .andExpect(jsonPath("$.response.newBadges[?(@.description == '%s')]", BadgeType.ADVENTURE_5.getDescription()).exists())
                .andExpect(jsonPath("$.response.newBadges[?(@.name == '%s')]", BadgeType.ENGINEER.name()).exists())
                .andExpect(jsonPath("$.response.newBadges[?(@.description == '%s')]", BadgeType.ENGINEER.getDescription()).exists())
                .andDo(print());
    }

    @Test
    @DisplayName("예술가(ARTIST) 배지 획득")
    void adventureBadge_ARTIST() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(8L, 37.542775, 127.073131));

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(28L, 37.542095, 127.080905);
        String content = objectMapper.writeValueAsString(adventureSaveDto);

        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.newBadges[?(@.name == '%s')]", BadgeType.ARTIST.name()).exists())
                .andExpect(jsonPath("$.response.newBadges[?(@.description == '%s')]", BadgeType.ARTIST.getDescription()).exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 회원의 탐험 기록 조회")
    void findAdventure() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        String accessToken = userLoginService.login(userEmail).getAccessToken();

        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(1L, 37.539927, 127.073006));
        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(2L, 37.540158, 127.073463));
        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(3L, 37.539314, 127.074319));
        adventureService.saveAdventure(userEmail, new AdventureSaveDto.Request(4L, 37.540099, 127.073976));

        // expected
        mockMvc.perform(get("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.length()").value(4))
                .andExpect(jsonPath("$.response.[?(@.landmarkId == '%s')]", 1).exists())
                .andExpect(jsonPath("$.response.[?(@.landmarkId == '%s')]", 2).exists())
                .andExpect(jsonPath("$.response.[?(@.landmarkId == '%s')]", 3).exists())
                .andExpect(jsonPath("$.response.[?(@.landmarkId == '%s')]", 4).exists())
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("특정 랜드마크에 가장 많이 방문한 회원 조회")
    void findMemberMostAdventure() throws Exception {
        // given
        String user1Email = "test@email.com";
        String user2Email = "test2@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(user1Email, "tester1", "컴퓨터공학부"));
        userRegisterService.registerUser(new UserRegisterDto.Request(user2Email, "tester2", "컴퓨터공학부"));
        String accessToken = userLoginService.login(user1Email).getAccessToken();

        // expected
        // 1. 해당 위치에 한 명도 방문한 적이 없는 경우
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.message").value("해당 장소에 방문한 회원이 없습니다."))
                .andExpect(jsonPath("$.response.count").value(0))
                .andDo(print());

        // 2. 한 번이라도 더 방문한 회원 응답
        adventureService.saveAdventure(user1Email, new AdventureSaveDto.Request(1L, 37.539927, 127.073006));
        adventureService.saveAdventure(user1Email, new AdventureSaveDto.Request(1L, 37.539927, 127.073006));
        adventureService.saveAdventure(user2Email, new AdventureSaveDto.Request(1L, 37.539927, 127.073006));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.nickname").value("tester1"))
                .andExpect(jsonPath("$.response.count").value(2))
                .andDo(print());

        // 3. 방문 횟수가 같다면, 방문한지 오래된 회원 응답
        adventureService.saveAdventure(user2Email, new AdventureSaveDto.Request(1L, 37.539927, 127.073006));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.nickname").value("tester1"))
                .andExpect(jsonPath("$.response.count").value(2))
                .andDo(print());

        // 4. 한 번이라도 더 방문한 회원 응답
        adventureService.saveAdventure(user2Email, new AdventureSaveDto.Request(1L, 37.539927, 127.073006));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.nickname").value("tester2"))
                .andExpect(jsonPath("$.response.count").value(3))
                .andDo(print());
    }
}