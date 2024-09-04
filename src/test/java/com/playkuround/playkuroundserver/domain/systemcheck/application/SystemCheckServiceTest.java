package com.playkuround.playkuroundserver.domain.systemcheck.application;

import com.playkuround.playkuroundserver.domain.appversion.dao.AppVersionRepository;
import com.playkuround.playkuroundserver.domain.appversion.domain.AppVersion;
import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.domain.appversion.dto.OSAndVersion;
import com.playkuround.playkuroundserver.domain.systemcheck.dao.SystemCheckRepository;
import com.playkuround.playkuroundserver.domain.systemcheck.domain.SystemCheck;
import com.playkuround.playkuroundserver.domain.systemcheck.dto.HealthCheckDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemCheckServiceTest {

    @InjectMocks
    private SystemCheckService systemCheckService;

    @Mock
    private SystemCheckRepository systemCheckRepository;

    @Mock
    private AppVersionRepository appVersionRepository;

    @Nested
    @DisplayName("시스템 점검 유무 조회하기")
    class findSystemAvailable {

        @Test
        @DisplayName("DB에 아무것도 저장되어 있지 않다면 false 반환")
        void success_1() {
            // given
            when(systemCheckRepository.findTopByOrderByUpdatedAtDesc())
                    .thenReturn(Optional.empty());

            // when
            boolean systemAvailable = systemCheckService.isSystemAvailable();

            // then
            assertThat(systemAvailable).isFalse();
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("DB에서 조회된 데이터를 기준으로 반환")
        void success_2(boolean available) {
            // given
            when(systemCheckRepository.findTopByOrderByUpdatedAtDesc())
                    .thenReturn(Optional.of(new SystemCheck(available)));

            // when
            boolean systemAvailable = systemCheckService.isSystemAvailable();

            // then
            assertThat(systemAvailable).isEqualTo(available);
        }
    }

    @Nested
    @DisplayName("시스템 점검 유무 변경")
    class changeSystemAvailable {

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @DisplayName("기존의 데이터를 모두 지우고 새로운 데이터를 저장")
        void success_1(boolean available) {
            // when
            systemCheckService.changeSystemAvailable(available);

            // then
            verify(systemCheckRepository, times(1)).deleteAll();

            ArgumentCaptor<SystemCheck> saveArgument = ArgumentCaptor.forClass(SystemCheck.class);
            verify(systemCheckRepository, times(1)).save(saveArgument.capture());
            assertThat(saveArgument.getValue().isAvailable()).isEqualTo(available);
        }
    }

    @Nested
    @DisplayName("Health Check")
    class healthCheck {

        @Test
        @DisplayName("시스템 사용가능 여부와 지원하는 앱 버전을 반환")
        void success_1() {
            // given
            when(systemCheckRepository.findTopByOrderByUpdatedAtDesc())
                    .thenReturn(Optional.of(new SystemCheck(true)));

            List<AppVersion> supportedAppVersion = List.of(
                    new AppVersion(OperationSystem.ANDROID, "1.0.0"),
                    new AppVersion(OperationSystem.ANDROID, "1.0.1"),
                    new AppVersion(OperationSystem.IOS, "1.0.3")
            );
            when(appVersionRepository.findAll())
                    .thenReturn(supportedAppVersion);

            // when
            HealthCheckDto healthCheckDto = systemCheckService.healthCheck();

            // then
            assertThat(healthCheckDto.systemAvailable()).isTrue();

            List<OSAndVersion> expected = supportedAppVersion.stream()
                    .map(appVersion -> new OSAndVersion(appVersion.getOs(), appVersion.getVersion()))
                    .toList();
            assertThat(healthCheckDto.supportAppVersionList()).hasSize(3)
                    .containsExactlyInAnyOrderElementsOf(expected);
        }
    }

}