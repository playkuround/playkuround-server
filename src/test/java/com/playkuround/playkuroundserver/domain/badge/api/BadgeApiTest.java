package com.playkuround.playkuroundserver.domain.badge.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.badge.application.BadgeService;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.BadgeSaveDto;
import com.playkuround.playkuroundserver.domain.user.application.UserLoginService;
import com.playkuround.playkuroundserver.domain.user.application.UserRegisterService;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc //MockMvc 사용
@SpringBootTest
@ActiveProfiles("test")
class BadgeApiTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserRegisterService userRegisterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFindDao userFindDao;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clean() {
        badgeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Badge 찾기 - 0개")
    void findBadgeZero() throws Exception {
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        User user = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(user).getAccessToken();

        mockMvc.perform(get("/api/badges")
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response", hasSize(0)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("Badge 찾기 - 3개")
    void findBadgeThree() throws Exception {
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        User user = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(user).getAccessToken();

        badgeService.registerBadge(user, BadgeType.ADVENTURE_5.name());
        badgeService.registerBadge(user, BadgeType.ENGINEER.name());
        badgeService.registerBadge(user, BadgeType.ATTENDANCE_3.name());

        mockMvc.perform(get("/api/badges")
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.[?(@.name == '%s')]", "ADVENTURE_5").exists())
                .andExpect(jsonPath("$.response.[?(@.name == '%s')]", "ENGINEER").exists())
                .andExpect(jsonPath("$.response.[?(@.name == '%s')]", "ATTENDANCE_3").exists())
                .andDo(print())
                .andReturn();
    }


    @Test
    @DisplayName("Badge 저장")
    void saveBadge() throws Exception {
        // given
        String userEmail = "test@email.com";
        userRegisterService.registerUser(new UserRegisterDto.Request(userEmail, "nickname", "컴퓨터공학부"));
        User savedUser = userFindDao.findByEmail(userEmail);
        String accessToken = userLoginService.login(savedUser).getAccessToken();

        BadgeSaveDto badgeSaveDto = new BadgeSaveDto("ATTENDANCE_3");
        String content = objectMapper.writeValueAsString(badgeSaveDto);

        // when
        mockMvc.perform(post("/api/badges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isCreated())
                .andDo(print());

        // then
        User user = userRepository.findByEmail(userEmail).get();
        List<Badge> badges = badgeRepository.findByUser(user);
        assertEquals(1L, badges.size());
        assertEquals(BadgeType.ATTENDANCE_3, badges.get(0).getBadgeType());
    }
}