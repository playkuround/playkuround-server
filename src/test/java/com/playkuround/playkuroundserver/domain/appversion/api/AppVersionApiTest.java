package com.playkuround.playkuroundserver.domain.appversion.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playkuround.playkuroundserver.domain.appversion.OSAndVersion;
import com.playkuround.playkuroundserver.domain.appversion.api.request.UpdateAppVersionRequest;
import com.playkuround.playkuroundserver.domain.appversion.dao.AppVersionRepository;
import com.playkuround.playkuroundserver.domain.appversion.domain.AppVersion;
import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.domain.user.dao.UserRepository;
import com.playkuround.playkuroundserver.domain.user.domain.Role;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
class AppVersionApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppVersionRepository appVersionRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void afterEach() {
        appVersionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("앱 버전 올리기")
    class updateAppVersion {

        @Test
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("새로운 앱 버전 추가")
        void success_1() throws Exception {
            // when
            List<UpdateAppVersionRequest.OsAndVersion> osAndVersions = List.of(
                    new UpdateAppVersionRequest.OsAndVersion("android", "2.0.3"),
                    new UpdateAppVersionRequest.OsAndVersion("android", "2.0.4"),
                    new UpdateAppVersionRequest.OsAndVersion("ios", "2.0.0"),
                    new UpdateAppVersionRequest.OsAndVersion("ios", "2.0.1")
            );

            UpdateAppVersionRequest updateAppVersionRequest = new UpdateAppVersionRequest(osAndVersions);
            String request = objectMapper.writeValueAsString(updateAppVersionRequest);

            mockMvc.perform(post("/api/admin/app-version")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            List<AppVersion> appVersions = appVersionRepository.findAll();
            List<OSAndVersion> osAndVersionList = appVersions.stream()
                    .map(appversion -> new OSAndVersion(appversion.getOs(), appversion.getVersion()))
                    .toList();
            assertThat(osAndVersionList).containsExactlyInAnyOrder(
                    new OSAndVersion(OperationSystem.ANDROID, "2.0.3"),
                    new OSAndVersion(OperationSystem.ANDROID, "2.0.4"),
                    new OSAndVersion(OperationSystem.IOS, "2.0.0"),
                    new OSAndVersion(OperationSystem.IOS, "2.0.1")
            );
        }

        @Test
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("기존에 지원하던 앱 버전을 완전히 덮어쓴다.")
        void success_2() throws Exception {
            // given
            appVersionRepository.save(new AppVersion(OperationSystem.ANDROID, "2.0.3"));
            appVersionRepository.save(new AppVersion(OperationSystem.IOS, "1.4.3"));

            // when
            List<UpdateAppVersionRequest.OsAndVersion> osAndVersions = List.of(
                    new UpdateAppVersionRequest.OsAndVersion("android", "2.0.3"),
                    new UpdateAppVersionRequest.OsAndVersion("android", "2.0.4"),
                    new UpdateAppVersionRequest.OsAndVersion("ios", "2.0.0"),
                    new UpdateAppVersionRequest.OsAndVersion("ios", "2.0.1")
            );

            UpdateAppVersionRequest updateAppVersionRequest = new UpdateAppVersionRequest(osAndVersions);
            String request = objectMapper.writeValueAsString(updateAppVersionRequest);

            mockMvc.perform(post("/api/admin/app-version")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isOk())
                    .andDo(print());

            // then
            List<AppVersion> appVersions = appVersionRepository.findAll();
            List<OSAndVersion> osAndVersionList = appVersions.stream()
                    .map(appversion -> new OSAndVersion(appversion.getOs(), appversion.getVersion()))
                    .toList();
            assertThat(osAndVersionList).containsExactlyInAnyOrder(
                    new OSAndVersion(OperationSystem.ANDROID, "2.0.3"),
                    new OSAndVersion(OperationSystem.ANDROID, "2.0.4"),
                    new OSAndVersion(OperationSystem.IOS, "2.0.0"),
                    new OSAndVersion(OperationSystem.IOS, "2.0.1")
            );
        }

        @Test
        @WithMockCustomUser(role = Role.ROLE_ADMIN)
        @DisplayName("존재하지 않는 OS이면 에러가 발생한다.")
        void fail_1() throws Exception {
            List<UpdateAppVersionRequest.OsAndVersion> osAndVersions = List.of(
                    new UpdateAppVersionRequest.OsAndVersion("NOTFOUND", "2.0.3"),
                    new UpdateAppVersionRequest.OsAndVersion("ios", "2.0.1")
            );

            UpdateAppVersionRequest updateAppVersionRequest = new UpdateAppVersionRequest(osAndVersions);
            String request = objectMapper.writeValueAsString(updateAppVersionRequest);

            mockMvc.perform(post("/api/admin/app-version")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andDo(print());
        }

        @Test
        @WithMockCustomUser(role = Role.ROLE_USER)
        @DisplayName("ROLE_ADMIN이 아니면 권한 에러가 발생한다.")
        void fail_2() throws Exception {
            mockMvc.perform(post("/api/admin/app-version")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

}