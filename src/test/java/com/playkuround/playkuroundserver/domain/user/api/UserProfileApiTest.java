package com.playkuround.playkuroundserver.domain.user.api;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.common.AppVersion;
import com.playkuround.playkuroundserver.domain.common.SystemCheck;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Major;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
class UserProfileApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser
    @DisplayName("회원 정보 조회 성공")
    void userProfileSuccess() throws Exception {
        // expected
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.highestScore").isEmpty())
                .andExpect(jsonPath("$.response.major").value(Major.컴퓨터공학부.name()))
                .andExpect(jsonPath("$.response.nickname").value("tester"))
                .andExpect(jsonPath("$.response.email").value("tester@konkuk.ac.kr"))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 중복 조회 - 중복일 때")
    void checkDuplicateWhenDuplication() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);

        // expected
        mockMvc.perform(get("/api/users/availability")
                        .param("nickname", "tester")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 중복 조회 - 중복이 아닐 때")
    void checkDuplicateWhenNotDuplication() throws Exception {
        // given
        User user = TestUtil.createUser();
        userRepository.save(user);

        // expected
        mockMvc.perform(get("/api/users/availability")
                        .param("nickname", "tester12")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response").value(true))
                .andDo(print());
    }

    @Nested
    @WithMockCustomUser
    @DisplayName("유저 알림 얻기")
    class getNotification {

        @Test
        @DisplayName("시스템이 사용 불가능할 때: name=system_check")
        void success_1() throws Exception {
            // given
            SystemCheck.changeSystemAvailable(false);
            String os = "android";
            String version = "2.0.0";
            AppVersion.changeLatestUpdatedVersion(os, version);

            // expect
            mockMvc.perform(get("/api/users/notification")
                            .queryParam("version", version)
                            .queryParam("os", os))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.response[0].name").value("system_check"))
                    .andDo(print());
        }

        @Test
        @DisplayName("앱 버전을 지원하지 않을 때: name=update")
        void success_2() throws Exception {
            // given
            SystemCheck.changeSystemAvailable(true);
            String os = "android";
            String version = "2.0.0";
            AppVersion.changeLatestUpdatedVersion(os, version);

            // expect
            mockMvc.perform(get("/api/users/notification")
                            .queryParam("version", "notSupport")
                            .queryParam("os", os))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.response[0].name").value("update"))
                    .andDo(print());
        }
    }
}

