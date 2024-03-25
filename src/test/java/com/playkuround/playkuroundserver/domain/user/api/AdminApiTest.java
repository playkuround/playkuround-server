package com.playkuround.playkuroundserver.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.common.AppVersion;
import com.playkuround.playkuroundserver.domain.common.SystemCheck;
import com.playkuround.playkuroundserver.domain.user.api.request.ManualBadgeSaveRequest;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.*;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @DisplayName("앱 버전 올리기")
    class updateAppVersion {

        @ParameterizedTest
        @ValueSource(strings = {"1.0.0", "1.0.1", "1.1.0", "2.0.0"})
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("앱 버전을 업데이트합니다.")
        void success_1(String version) throws Exception {
            // expect
            mockMvc.perform(post("/api/admin/app-version")
                            .queryParam("version", version)
                            .queryParam("os", "android"))
                    .andExpect(status().isOk())
                    .andDo(print());

            assertThat(AppVersion.isLatestUpdatedVersion("android", version)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"android", "ios"})
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("OS 별로 앱 버전을 별도로 관리한다.")
        void success_2(String os) throws Exception {
            String appVersion = "12.0.0";
            // expect
            mockMvc.perform(post("/api/admin/app-version")
                            .queryParam("version", appVersion)
                            .queryParam("os", os))
                    .andExpect(status().isOk())
                    .andDo(print());

            assertThat(AppVersion.isLatestUpdatedVersion(os, appVersion)).isTrue();
        }

        @Test
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("존재하지 않는 OS이면 에러가 발생한다.")
        void fail_1() throws Exception {
            mockMvc.perform(post("/api/admin/app-version")
                            .queryParam("version", "12.0.0")
                            .queryParam("os", "notFound"))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @WithMockCustomUser(role = Role.ROLE_USER)
        @DisplayName("ROLE_ADMIN이 아니면 권한 에러가 발생한다.")
        void fail_2() throws Exception {
            mockMvc.perform(post("/api/admin/app-version")
                            .queryParam("version", "12.0.0")
                            .queryParam("os", "android"))
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("시스템 점검 유무 변경하기")
    class changeSystemAvailable {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("시스템 변경 유무를 변경합니다.")
        void success_1(boolean available) throws Exception {
            // expect
            mockMvc.perform(post("/api/admin/system-available")
                            .queryParam("available", String.valueOf(available)))
                    .andExpect(status().isOk())
                    .andDo(print());

            assertThat(SystemCheck.isSystemAvailable()).isEqualTo(available);
        }
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
            Set<Notification> notification = optionalUser.get().getNotification();
            assertThat(notification)
                    .containsOnly(new Notification(NotificationEnum.NEW_BADGE, BadgeType.MONTHLY_RANKING_1.name()));
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