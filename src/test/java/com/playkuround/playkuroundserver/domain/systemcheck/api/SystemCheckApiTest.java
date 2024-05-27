package com.playkuround.playkuroundserver.domain.systemcheck.api;

import com.playkuround.playkuroundserver.domain.systemcheck.dao.SystemCheckRepository;
import com.playkuround.playkuroundserver.domain.systemcheck.domain.SystemCheck;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
import com.playkuround.playkuroundserver.securityConfig.WithMockCustomUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
class SystemCheckApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemCheckRepository systemCheckRepository;

    @AfterEach
    void clean() {
        systemCheckRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("시스템 점검 유무 변경하기")
    class changeSystemAvailable {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("DB에 아무것도 저장되어 있지 않으면 새롭게 저장된다.")
        void success_1(boolean available) throws Exception {
            // expect
            mockMvc.perform(post("/api/admin/system-available")
                            .queryParam("available", String.valueOf(available)))
                    .andExpect(status().isOk())
                    .andDo(print());

            List<SystemCheck> systemCheckList = systemCheckRepository.findAll();
            assertThat(systemCheckList).hasSize(1);
            assertThat(systemCheckList.get(0).isAvailable()).isEqualTo(available);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("DB에 1개 이상 저장되어 있다면 1개만 남기고 지워진다.")
        void success_2(boolean available) throws Exception {
            // given
            systemCheckRepository.save(new SystemCheck(true));
            systemCheckRepository.save(new SystemCheck(false));

            // expect
            mockMvc.perform(post("/api/admin/system-available")
                            .queryParam("available", String.valueOf(available)))
                    .andExpect(status().isOk())
                    .andDo(print());

            List<SystemCheck> systemCheckList = systemCheckRepository.findAll();
            assertThat(systemCheckList).hasSize(1);
            assertThat(systemCheckList.get(0).isAvailable()).isEqualTo(available);
        }
    }

}