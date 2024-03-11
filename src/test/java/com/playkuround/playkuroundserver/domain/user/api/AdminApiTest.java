package com.playkuround.playkuroundserver.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.badge.dto.request.ManualBadgeSaveRequest;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
class AdminApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("뱃지 수동 등록")
    class saveManualBadge {

        @Test
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("정상 뱃지 수동 등록 : 개인 메시지 저장 안함")
        void success_1() throws Exception {
            // given
            User user = TestUtil.createUser("aa@konkuk.ac.kr", "newNickname", Major.건축학부);
            userRepository.save(user);

            ManualBadgeSaveRequest manualBadgeSaveRequest
                    = new ManualBadgeSaveRequest(user.getEmail(), BadgeType.MONTHLY_RANKING_1.name(), false);
            String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

            // expect
            mockMvc.perform(post("/api/badges/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.response").value(true))
                    .andDo(print());

            List<Badge> badges = badgeRepository.findByUser(user);
            assertThat(badges).hasSize(1);
            assertThat(badges.get(0).getBadgeType()).isEqualTo(BadgeType.MONTHLY_RANKING_1);
            assertThat(user.getNotification()).isNull();
        }

        @Test
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("정상 뱃지 수동 등록 : 개인 메시지 저장")
        void success_2() throws Exception {
            // given
            User user = TestUtil.createUser("aa@konkuk.ac.kr", "test", Major.건축학부);
            userRepository.save(user);

            ManualBadgeSaveRequest manualBadgeSaveRequest
                    = new ManualBadgeSaveRequest(user.getEmail(), BadgeType.MONTHLY_RANKING_1.name(), true);
            String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

            // expect
            mockMvc.perform(post("/api/admin/badges/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.response").value(true))
                    .andDo(print());

            List<Badge> badges = badgeRepository.findByUser(user);
            assertThat(badges).hasSize(1);
            assertThat(badges.get(0).getBadgeType()).isEqualTo(BadgeType.MONTHLY_RANKING_1);

            Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
            assertThat(optionalUser.get().getNotification()).isEqualTo("new_badge#" + BadgeType.MONTHLY_RANKING_1.name());
        }

        @Test
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("이미 가지고 있는 뱃지면 false를 반환한다")
        void fail_1() throws Exception {
            // given
            User user = TestUtil.createUser("aa@konkuk.ac.kr", "test", Major.건축학부);
            userRepository.save(user);
            badgeRepository.save(new Badge(user, BadgeType.MONTHLY_RANKING_1));

            ManualBadgeSaveRequest manualBadgeSaveRequest
                    = new ManualBadgeSaveRequest(user.getEmail(), BadgeType.MONTHLY_RANKING_1.name(), true);
            String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

            // expect
            mockMvc.perform(post("/api/admin/badges/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.response").value(false))
                    .andDo(print());

            List<Badge> badges = badgeRepository.findByUser(user);
            assertThat(badges).hasSize(1);
            assertThat(user.getNotification()).isNull();
        }

        @Test
        @WithMockCustomUser(role = Role.ROLE_USER)
        @DisplayName("admin 권한이 없으면 권한 에러가 발생한다")
        void fail_2() throws Exception {
            // given
            ManualBadgeSaveRequest manualBadgeSaveRequest
                    = new ManualBadgeSaveRequest("test@konkuk.ac.kr", BadgeType.MONTHLY_RANKING_1.name(), true);
            String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

            // expect
            mockMvc.perform(post("/api/admin/badges/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }
}