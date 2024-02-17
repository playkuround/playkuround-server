package com.playkuround.playkuroundserver.domain.badge.api;

import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
class BadgeApiTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
        badgeRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser
    void Badge_찾기_0개() throws Exception {
        mockMvc.perform(get("/api/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response", hasSize(0)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockCustomUser
    void Badge_찾기_3개() throws Exception {
        User user = userRepository.findAll().get(0);
        badgeRepository.save(new Badge(user, BadgeType.ATTENDANCE_FOUNDATION_DAY));
        badgeRepository.save(new Badge(user, BadgeType.ATTENDANCE_30));
        badgeRepository.save(new Badge(user, BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_50));

        mockMvc.perform(get("/api/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.[?(@.name == '%s')]", "ATTENDANCE_FOUNDATION_DAY").exists())
                .andExpect(jsonPath("$.response.[?(@.name == '%s')]", "ATTENDANCE_30").exists())
                .andExpect(jsonPath("$.response.[?(@.name == '%s')]", "COLLEGE_OF_BUSINESS_ADMINISTRATION_50").exists())
                .andDo(print())
                .andReturn();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("오리의 꿈 뱃지 획득 성공")
    void saveTheDreamOfDuckBadge_1() throws Exception {
        // expect
        mockMvc.perform(post("/api/badges/dream-of-duck"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response").value(true))
                .andDo(print())
                .andReturn();

        List<Badge> badges = badgeRepository.findByUser(userRepository.findAll().get(0));
        assertThat(badges).hasSize(1);
        assertThat(badges.get(0).getBadgeType()).isEqualTo(BadgeType.THE_DREAM_OF_DUCK);
    }

    @Test
    @WithMockCustomUser
    @DisplayName("오리의 꿈 뱃지 획득 : 이미 가지고 있다면 false가 반환된다")
    void saveTheDreamOfDuckBadge_2() throws Exception {
        // given
        User user = userRepository.findAll().get(0);
        badgeRepository.save(new Badge(user, BadgeType.THE_DREAM_OF_DUCK));

        // expect
        mockMvc.perform(post("/api/badges/dream-of-duck"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response").value(false))
                .andDo(print())
                .andReturn();

        List<Badge> badges = badgeRepository.findByUser(userRepository.findAll().get(0));
        assertThat(badges).hasSize(1);
        assertThat(badges.get(0).getBadgeType()).isEqualTo(BadgeType.THE_DREAM_OF_DUCK);
    }
}