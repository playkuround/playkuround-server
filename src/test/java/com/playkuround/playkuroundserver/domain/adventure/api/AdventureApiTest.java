package com.playkuround.playkuroundserver.domain.adventure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.adventure.application.AdventureService;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import com.playkuround.playkuroundserver.domain.adventure.dto.AdventureSaveDto;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.domain.user.dto.UserRegisterDto;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
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
    private UserFindDao userFindDao;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private AdventureRepository adventureRepository;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clean() {
        adventureRepository.deleteAll();
        badgeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("탐험 저장 + 첫 탐험 배지(ADVENTURE_1) 획득")
    void saveAdventure() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        User savedUser = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(savedUser).getAccessToken();

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(1L, 37.539765, 127.073215);
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
        User user = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(user).getAccessToken();

        adventureService.saveAdventure(user, new AdventureSaveDto.Request(2L, 37.540296, 127.073410));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(3L, 37.539310, 127.074590));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(4L, 37.540184, 127.074179));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(5L, 37.540901, 127.074055));

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(1L, 37.539765, 127.073215);
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
        User user = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(user).getAccessToken();

        adventureService.saveAdventure(user, new AdventureSaveDto.Request(22L, 37.541655, 127.078769));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(23L, 37.542004, 127.079563));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(24L, 37.541226, 127.079357));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(25L, 37.540455, 127.079304));

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(26L, 37.541491, 127.080565);
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
    @DisplayName("CEO 배지 획득")
    void adventureBadge_CEO() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        User user = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(user).getAccessToken();

        adventureService.saveAdventure(user, new AdventureSaveDto.Request(14L, 37.544018, 127.075141));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(15L, 37.544319, 127.076184));

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(19L, 37.542602, 127.078250);
        String content = objectMapper.writeValueAsString(adventureSaveDto);

        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response.newBadges.size()").value(1))
                .andExpect(jsonPath("$.response.newBadges[?(@.name == '%s')]", BadgeType.CEO.name()).exists())
                .andExpect(jsonPath("$.response.newBadges[?(@.description == '%s')]", BadgeType.CEO.getDescription()).exists())
                .andDo(print());
    }

    @Test
    @DisplayName("예술가(ARTIST) 배지 획득")
    void adventureBadge_ARTIST() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        User user = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(user).getAccessToken();

        adventureService.saveAdventure(user, new AdventureSaveDto.Request(8L, 37.542908, 127.072815));

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(28L, 37.542220, 127.080961);
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
        User user = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(user).getAccessToken();

        adventureService.saveAdventure(user, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(2L, 37.540296, 127.073410));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(3L, 37.539310, 127.074590));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(4L, 37.540184, 127.074179));

        // expected
        mockMvc.perform(get("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.landmarkIdList.length()").value(4))
                .andExpect(jsonPath("$.response.landmarkIdList", 1).exists())
                .andExpect(jsonPath("$.response.landmarkIdList", 2).exists())
                .andExpect(jsonPath("$.response.landmarkIdList", 3).exists())
                .andExpect(jsonPath("$.response.landmarkIdList", 4).exists())
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("각 랜드마크는 하루에 한번만 탐험이 가능")
    void failsSaveAdventure() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        User SavedUser = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(SavedUser).getAccessToken();

        AdventureSaveDto.Request adventureSaveDto = new AdventureSaveDto.Request(1L, 37.539765, 127.073215);
        adventureService.saveAdventure(SavedUser, adventureSaveDto);
        String content = objectMapper.writeValueAsString(adventureSaveDto);

        // expected
        mockMvc.perform(post("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.errorResponse.status").value(400))
                .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.DUPLICATE_ADVENTURE.getCode()))
                .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.DUPLICATE_ADVENTURE.getMessage()))
                .andDo(print());

        Adventure adventure = adventureRepository.findAll().get(0);
        assertEquals(1L, adventureRepository.count());
        assertEquals(1L, adventure.getLandmark().getId());

        User user = userRepository.findAll().get(0);
        assertEquals(1L, userRepository.count());
        assertEquals(user.getId(), adventure.getUser().getId());
    }

    @Test
    @Disabled("각 랜드마크는 하루에 한번만 탐험이 가능 - 어떻게 테스트를?")
    @DisplayName("로그인 회원의 탐험 기록 조회 - 중복 랜드마크 존재 시, 1번만 등장해야 함")
    void findAdventureWhenDuplicationLandmark() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        User user = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(user).getAccessToken();

        adventureService.saveAdventure(user, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(2L, 37.540296, 127.073410));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(3L, 37.539310, 127.074590));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(3L, 37.539310, 127.074590));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(4L, 37.540184, 127.074179));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(4L, 37.540184, 127.074179));
        adventureService.saveAdventure(user, new AdventureSaveDto.Request(4L, 37.540184, 127.074179));

        // expected
        mockMvc.perform(get("/api/adventures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.landmarkIdList.length()").value(4))
                .andExpect(jsonPath("$.response.landmarkIdList", 1).exists())
                .andExpect(jsonPath("$.response.landmarkIdList", 2).exists())
                .andExpect(jsonPath("$.response.landmarkIdList", 3).exists())
                .andExpect(jsonPath("$.response.landmarkIdList", 4).exists())
                .andDo(print())
                .andReturn();
    }

    @Test
    @Disabled("각 랜드마크는 하루에 한번만 탐험이 가능 - 어떻게 테스트를?")
    @DisplayName("특정 랜드마크에 가장 많이 방문한 회원 조회(2명)")
    void findMemberMostAdventureWhenTwoPeople() throws Exception {
        // given
        String user1Email = "test@email.com";
        String user2Email = "test2@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(user1Email, "tester1", "컴퓨터공학부"));
        userRegisterService.registerUser(new UserRegisterDto.Request(user2Email, "tester2", "컴퓨터공학부"));
        User user1 = userFindDao.findByEmail(user1Email);
        User user2 = userFindDao.findByEmail(user2Email);
        String accessToken = userLoginService.login(user1).getAccessToken();

        // expected
        // 1. 해당 위치에 한 명도 방문한 적이 없는 경우
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // 2. 한 번이라도 더 방문한 회원 응답
        adventureService.saveAdventure(user1, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        adventureService.saveAdventure(user1, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        adventureService.saveAdventure(user2, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.top5Users.length()", "2").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.nickname == '%s')]", "tester1").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.count == '%s')]", "2").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.nickname == '%s')]", "tester2").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.me.count").value(2))
                .andExpect(jsonPath("$.response.me.ranking").value(1))
                .andDo(print());

        // 3. 방문 횟수가 같다면, 방문한지 오래된 회원 응답
        adventureService.saveAdventure(user2, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.top5Users.length()", "2").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.nickname == '%s')]", "tester1").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.count == '%s')]", "2").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.nickname == '%s')]", "tester2").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.count == '%s')]", "2").exists())
                .andExpect(jsonPath("$.response.me.count").value(2))
                .andExpect(jsonPath("$.response.me.ranking").value(1))
                .andDo(print());

        // 4. 한 번이라도 더 방문한 회원 응답
        adventureService.saveAdventure(user2, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.top5Users.length()", "2").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.nickname == '%s')]", "tester2").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.count == '%s')]", "3").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.nickname == '%s')]", "tester1").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.count == '%s')]", "2").exists())
                .andExpect(jsonPath("$.response.me.count").value(2))
                .andExpect(jsonPath("$.response.me.ranking").value(2))
                .andDo(print());
    }

    @Test
    @Disabled("각 랜드마크는 하루에 한번만 탐험이 가능 - 어떻게 테스트를?")
    @DisplayName("특정 랜드마크에 가장 많이 방문한 회원 조회(6명)")
    void findMemberMostAdventureWhenSixPeople() throws Exception {
        // given
        String user1Email = "test@email.com";
        String user2Email = "test2@email.com";
        String user3Email = "test3@email.com";
        String user4Email = "test4@email.com";
        String user5Email = "test5@email.com";
        String user6Email = "test6@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(user1Email, "tester1", "컴퓨터공학부"));
        userRegisterService.registerUser(new UserRegisterDto.Request(user2Email, "tester2", "컴퓨터공학부"));
        userRegisterService.registerUser(new UserRegisterDto.Request(user3Email, "tester3", "컴퓨터공학부"));
        userRegisterService.registerUser(new UserRegisterDto.Request(user4Email, "tester4", "컴퓨터공학부"));
        userRegisterService.registerUser(new UserRegisterDto.Request(user5Email, "tester5", "컴퓨터공학부"));
        userRegisterService.registerUser(new UserRegisterDto.Request(user6Email, "tester6", "컴퓨터공학부"));
        User user1 = userFindDao.findByEmail(user1Email);
        User user2 = userFindDao.findByEmail(user2Email);
        User user3 = userFindDao.findByEmail(user3Email);
        User user4 = userFindDao.findByEmail(user4Email);
        User user5 = userFindDao.findByEmail(user5Email);
        User user6 = userFindDao.findByEmail(user6Email);

        String accessToken = userLoginService.login(user1).getAccessToken();

        // expected
        // 1. 3명이 한번씩 방문
        adventureService.saveAdventure(user2, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        adventureService.saveAdventure(user3, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        adventureService.saveAdventure(user4, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.top5Users.length()", "3").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.nickname == '%s')]", "tester2").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.nickname == '%s')]", "tester3").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.top5Users[2].[?(@.nickname == '%s')]", "tester4").exists())
                .andExpect(jsonPath("$.response.top5Users[2].[?(@.count == '%s')]", "1").exists())
                .andDo(print());

        // 2. 6명이 한번씩 방문
        adventureService.saveAdventure(user1, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        adventureService.saveAdventure(user5, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        adventureService.saveAdventure(user6, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.top5Users.length()", "5").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.nickname == '%s')]", "tester2").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.nickname == '%s')]", "tester3").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.top5Users[2].[?(@.nickname == '%s')]", "tester4").exists())
                .andExpect(jsonPath("$.response.top5Users[2].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.top5Users[3].[?(@.nickname == '%s')]", "tester1").exists())
                .andExpect(jsonPath("$.response.top5Users[3].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.top5Users[4].[?(@.nickname == '%s')]", "tester5").exists())
                .andExpect(jsonPath("$.response.top5Users[4].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.me.count").value(1))
                .andExpect(jsonPath("$.response.me.ranking").value(4))
                .andDo(print());

        // 3. 2명이 한번씩 더 방문
        adventureService.saveAdventure(user5, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        adventureService.saveAdventure(user6, new AdventureSaveDto.Request(1L, 37.539765, 127.073215));
        mockMvc.perform(get("/api/adventures/1/most")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.top5Users.length()", "5").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.nickname == '%s')]", "tester5").exists())
                .andExpect(jsonPath("$.response.top5Users[0].[?(@.count == '%s')]", "2").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.nickname == '%s')]", "tester6").exists())
                .andExpect(jsonPath("$.response.top5Users[1].[?(@.count == '%s')]", "2").exists())
                .andExpect(jsonPath("$.response.top5Users[2].[?(@.nickname == '%s')]", "tester2").exists())
                .andExpect(jsonPath("$.response.top5Users[2].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.top5Users[3].[?(@.nickname == '%s')]", "tester3").exists())
                .andExpect(jsonPath("$.response.top5Users[3].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.top5Users[4].[?(@.nickname == '%s')]", "tester4").exists())
                .andExpect(jsonPath("$.response.top5Users[4].[?(@.count == '%s')]", "1").exists())
                .andExpect(jsonPath("$.response.me.count").value(1))
                .andExpect(jsonPath("$.response.me.ranking").value(6))
                .andDo(print());
    }
}