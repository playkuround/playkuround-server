package com.playkuround.playkuroundserver.domain.badge.api;

import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
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
        badgeRepository.save(new Badge(user, BadgeType.ENGINEER));
        badgeRepository.save(new Badge(user, BadgeType.ADVENTURE_5));
        badgeRepository.save(new Badge(user, BadgeType.ATTENDANCE_3));

        mockMvc.perform(get("/api/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.[?(@.name == '%s')]", "ADVENTURE_5").exists())
                .andExpect(jsonPath("$.response.[?(@.name == '%s')]", "ENGINEER").exists())
                .andExpect(jsonPath("$.response.[?(@.name == '%s')]", "ATTENDANCE_3").exists())
                .andDo(print())
                .andReturn();
    }
}