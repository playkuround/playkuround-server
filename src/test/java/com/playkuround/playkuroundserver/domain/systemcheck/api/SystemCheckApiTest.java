package com.playkuround.playkuroundserver.domain.systemcheck.api;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.appversion.dao.AppVersionRepository;
import com.playkuround.playkuroundserver.domain.appversion.domain.AppVersion;
import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.domain.systemcheck.api.response.HealthCheckResponse;
import com.playkuround.playkuroundserver.domain.systemcheck.dao.SystemCheckRepository;
import com.playkuround.playkuroundserver.domain.systemcheck.domain.SystemCheck;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private AppVersionRepository appVersionRepository;

    @AfterEach
    void clean() {
        appVersionRepository.deleteAll();
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

    @Nested
    @DisplayName("Health Check")
    class healthCheck {

        @Test
        @DisplayName("시스템 사용가능 여부와 지원하는 앱 버전을 반환")
        void success_1() throws Exception {
            // given
            systemCheckRepository.save(new SystemCheck(true));
            appVersionRepository.save(new AppVersion(OperationSystem.ANDROID, "1.0.0"));
            appVersionRepository.save(new AppVersion(OperationSystem.ANDROID, "1.0.1"));
            appVersionRepository.save(new AppVersion(OperationSystem.IOS, "1.0.2"));

            // expect
            MvcResult mvcResult = mockMvc.perform(get("/api/system-available"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            String json = mvcResult.getResponse().getContentAsString();
            HealthCheckResponse response = TestUtil.convertFromJsonStringToObject(json, HealthCheckResponse.class);

            assertThat(response.isSystemAvailable()).isTrue();
            assertThat(response.getSupportAppVersionList())
                    .filteredOn(healthCheckResponse -> healthCheckResponse.getOs().equals(OperationSystem.ANDROID.name()))
                    .flatExtracting(HealthCheckResponse.OSAndVersions::getVersion)
                    .containsExactlyInAnyOrder("1.0.0", "1.0.1");
            assertThat(response.getSupportAppVersionList())
                    .filteredOn(healthCheckResponse -> healthCheckResponse.getOs().equals(OperationSystem.IOS.name()))
                    .flatExtracting(HealthCheckResponse.OSAndVersions::getVersion)
                    .containsExactlyInAnyOrder("1.0.2");
        }

        @Test
        @DisplayName("특정 OS만 지원하는 경우에 버전에는 빈 배열이 반환된다.")
        void success_2() throws Exception {
            // given
            systemCheckRepository.save(new SystemCheck(false));
            appVersionRepository.save(new AppVersion(OperationSystem.ANDROID, "1.0.0"));
            appVersionRepository.save(new AppVersion(OperationSystem.ANDROID, "1.0.1"));

            // expect
            MvcResult mvcResult = mockMvc.perform(get("/api/system-available"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            String json = mvcResult.getResponse().getContentAsString();
            HealthCheckResponse response = TestUtil.convertFromJsonStringToObject(json, HealthCheckResponse.class);

            assertThat(response.isSystemAvailable()).isFalse();
            assertThat(response.getSupportAppVersionList())
                    .filteredOn(healthCheckResponse -> healthCheckResponse.getOs().equals(OperationSystem.ANDROID.name()))
                    .flatExtracting(HealthCheckResponse.OSAndVersions::getVersion)
                    .containsExactlyInAnyOrder("1.0.0", "1.0.1");
            assertThat(response.getSupportAppVersionList())
                    .filteredOn(healthCheckResponse -> healthCheckResponse.getOs().equals(OperationSystem.IOS.name()))
                    .flatExtracting(HealthCheckResponse.OSAndVersions::getVersion)
                    .isEmpty();
        }
    }

}