package com.playkuround.playkuroundserver.domain.badge.api;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

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

    @Test
    @WithMockCustomUser(role = Role.ROLE_ADMIN)
    @DisplayName("뱃지 수동 등록")
    void saveManualBadge_1() throws Exception {
        // given
        User user = TestUtil.createUser("aa@konkuk.ac.kr", "test", Major.건축학부);
        userRepository.save(user);

        ManualBadgeSaveRequest manualBadgeSaveRequest
                = new ManualBadgeSaveRequest(user.getEmail(), BadgeType.MONTHLY_RANKING_1.name(), false);
        String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

        // expect
        mockMvc.perform(post("/api/badges/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
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
    @DisplayName("뱃지 수동 등록 : 개인 메시지 저장")
    void saveManualBadge_2() throws Exception {
        // given
        User user = TestUtil.createUser("aa@konkuk.ac.kr", "test", Major.건축학부);
        userRepository.save(user);

        ManualBadgeSaveRequest manualBadgeSaveRequest
                = new ManualBadgeSaveRequest(user.getEmail(), BadgeType.MONTHLY_RANKING_1.name(), true);
        String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

        // expect
        mockMvc.perform(post("/api/badges/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
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
    @DisplayName("뱃지 수동 등록 : 이미 가지고 있는 뱃지면 false를 반환한다.")
    void saveManualBadge_3() throws Exception {
        // given
        User user = TestUtil.createUser("aa@konkuk.ac.kr", "test", Major.건축학부);
        userRepository.save(user);
        badgeRepository.save(new Badge(user, BadgeType.MONTHLY_RANKING_1));

        ManualBadgeSaveRequest manualBadgeSaveRequest
                = new ManualBadgeSaveRequest(user.getEmail(), BadgeType.MONTHLY_RANKING_1.name(), true);
        String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

        // expect
        mockMvc.perform(post("/api/badges/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response").value(false))
                .andDo(print());

        List<Badge> badges = badgeRepository.findByUser(user);
        assertThat(badges).hasSize(1);
        assertThat(user.getNotification()).isNull();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("뱃지 수동 등록 : admin 권한이 없으면 403을 반환한다.")
    void saveManualBadge_4() throws Exception {
        // given
        ManualBadgeSaveRequest manualBadgeSaveRequest
                = new ManualBadgeSaveRequest("test@konkuk.ac.kr", BadgeType.MONTHLY_RANKING_1.name(), true);
        String request = objectMapper.writeValueAsString(manualBadgeSaveRequest);

        // expect
        mockMvc.perform(post("/api/badges/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}