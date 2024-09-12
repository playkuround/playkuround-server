package com.playkuround.playkuroundserver.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.IntegrationControllerTest;
import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.badge.dao.BadgeRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.user.api.request.ManualBadgeSaveRequest;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.*;
import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationControllerTest
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
        badgeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Nested
    @WithMockCustomUser(role = Role.ROLE_ADMIN)
    @DisplayName("배지 수동 등록")
    class saveManualBadge {

        @Test
        @DisplayName("기존에 가지고 있지 않는 배지인 경우만 저장된다.")
        void success_1() throws Exception {
            // given
            List<User> users = List.of(
                    TestUtil.createUser("user1@konkuk.ac.kr", "user1", Major.경영학과),
                    TestUtil.createUser("user2@konkuk.ac.kr", "user2", Major.컴퓨터공학부),
                    TestUtil.createUser("user3@konkuk.ac.kr", "user3", Major.국제무역학과)
            );
            userRepository.saveAll(users);

            BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;
            badgeRepository.save(new Badge(users.get(0), BadgeType.MONTHLY_RANKING_1));

            List<String> emails = users.stream()
                    .map(User::getEmail)
                    .toList();
            ManualBadgeSaveRequest manualBadgeSaveRequest
                    = new ManualBadgeSaveRequest(emails, badgeType.name(), false);
            String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

            // expect
            mockMvc.perform(post("/api/admin/badges/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.response").value(2))
                    .andDo(print());

            List<Badge> badges = badgeRepository.findAll();
            assertThat(badges).hasSize(3)
                    .extracting("user.id", "badgeType")
                    .containsExactlyInAnyOrder(
                            tuple(users.get(0).getId(), badgeType),
                            tuple(users.get(1).getId(), badgeType),
                            tuple(users.get(2).getId(), badgeType)
                    );
        }

        @Test
        @DisplayName("개인 메시지 저장 설정이 true인 경우, 새롭게 배지가 저장된 유저에게 메시지가 저장된다")
        void success_2() throws Exception {
            // given
            List<User> existUsers = List.of(
                    TestUtil.createUser("user1@konkuk.ac.kr", "user1", Major.컴퓨터공학부),
                    TestUtil.createUser("user2@konkuk.ac.kr", "user2", Major.국제무역학과)
            );
            List<User> notExistUsers = List.of(
                    TestUtil.createUser("user3@konkuk.ac.kr", "user3", Major.건축학부),
                    TestUtil.createUser("user4@konkuk.ac.kr", "user4", Major.국어국문학과)
            );

            List<User> allUsers = Stream.concat(existUsers.stream(), notExistUsers.stream()).toList();
            userRepository.saveAll(allUsers);

            BadgeType badgeType = BadgeType.MONTHLY_RANKING_1;
            List<Badge> badges = existUsers.stream()
                    .map(user -> new Badge(user, badgeType))
                    .toList();
            badgeRepository.saveAll(badges);

            List<String> emails = allUsers.stream()
                    .map(User::getEmail)
                    .toList();
            ManualBadgeSaveRequest manualBadgeSaveRequest
                    = new ManualBadgeSaveRequest(emails, badgeType.name(), true);
            String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

            // expect
            mockMvc.perform(post("/api/admin/badges/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.response").value(notExistUsers.size()))
                    .andDo(print());

            List<String> notExistUserEmails = notExistUsers.stream()
                    .map(User::getEmail)
                    .toList();
            notExistUsers = userRepository.findByEmailIn(notExistUserEmails);
            assertThat(notExistUsers).hasSize(notExistUsers.size())
                    .extracting("notification", Set.class)
                    .allMatch(notifications -> notifications.contains(new Notification(NotificationEnum.NEW_BADGE, badgeType.name())));

            List<String> existUserEmails = existUsers.stream()
                    .map(User::getEmail)
                    .toList();
            existUsers = userRepository.findByEmailIn(existUserEmails);
            assertThat(existUsers).hasSize(existUsers.size())
                    .extracting("notification", Set.class)
                    .allMatch(Set::isEmpty);
        }

        @Test
        @DisplayName("존재하지 않는 이메일이 하나라도 존재하면 예외를 던진다.")
        void fail_1() throws Exception {
            // given
            List<User> users = List.of(
                    TestUtil.createUser("user1@konkuk.ac.kr", "user1", Major.경영학과),
                    TestUtil.createUser("user2@konkuk.ac.kr", "user2", Major.컴퓨터공학부)
            );
            userRepository.saveAll(users);

            List<String> emails = new ArrayList<>();
            users.stream()
                    .map(User::getEmail)
                    .forEach(emails::add);
            emails.add("notFoundUser@konkuk.ac.kr");
            ManualBadgeSaveRequest manualBadgeSaveRequest
                    = new ManualBadgeSaveRequest(emails, BadgeType.MONTHLY_RANKING_1.name(), false);
            String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

            // expect
            mockMvc.perform(post("/api/admin/badges/manual")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorResponse.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.errorResponse.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

    }
}