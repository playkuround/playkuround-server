package com.playkuround.playkuroundserver.domain.badge.api;

import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
class BadgeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @AfterEach
    void clean() {
        badgeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @WithMockCustomUser
    @DisplayName("뱃지 조회하기")
    class findBadge {

        @Test
        @DisplayName("0개 조회")
        void success_1() throws Exception {
            mockMvc.perform(get("/api/badges"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.response.size()").value(0))
                    .andDo(print());
        }

        @Test
        @DisplayName("3개 조회")
        void success_2() throws Exception {
            // given
            User user = userRepository.findAll().get(0);
            badgeRepository.save(new Badge(user, BadgeType.ATTENDANCE_FOUNDATION_DAY));
            badgeRepository.save(new Badge(user, BadgeType.ATTENDANCE_30));
            badgeRepository.save(new Badge(user, BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_50));

            // expect
            mockMvc.perform(get("/api/badges"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.response.size()").value(3))
                    .andExpect(jsonPath("$.response.[*].name").value(
                            containsInAnyOrder(
                                    BadgeType.ATTENDANCE_30.name(),
                                    BadgeType.ATTENDANCE_FOUNDATION_DAY.name(),
                                    BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION_50.name()
                            )))
                    .andDo(print());
        }
    }

    @Nested
    @WithMockCustomUser
    @DisplayName("오리의 꿈 뱃지 획득")
    class saveTheDreamOfDuckBadge {

        @Test
        @DisplayName("기존에 안가지고 있었다면 새롭게 뱃지를 획득하고 true가 반환된다")
        void success_1() throws Exception {
            mockMvc.perform(post("/api/badges/dream-of-duck"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.response").value(true))
                    .andDo(print());

            List<Badge> badges = badgeRepository.findByUser(userRepository.findAll().get(0));
            assertThat(badges).hasSize(1);
            assertThat(badges.get(0).getBadgeType()).isEqualTo(BadgeType.THE_DREAM_OF_DUCK);
        }

        @Test
        @WithMockCustomUser
        @DisplayName("기존에 이미 가지고 있었다면 false가 반환된다")
        void fail_1() throws Exception {
            // given
            User user = userRepository.findAll().get(0);
            badgeRepository.save(new Badge(user, BadgeType.THE_DREAM_OF_DUCK));

            // expect
            mockMvc.perform(post("/api/badges/dream-of-duck"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.response").value(false))
                    .andDo(print());
        }
    }

}