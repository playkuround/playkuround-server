package com.playkuround.playkuroundserver.domain.appversion.application;

import com.playkuround.playkuroundserver.domain.appversion.dao.AppVersionRepository;
import com.playkuround.playkuroundserver.domain.appversion.domain.AppVersion;
import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.domain.appversion.dto.OSAndVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppVersionServiceTest {

    @InjectMocks
    private AppVersionService appVersionService;

    @Mock
    private AppVersionRepository appVersionRepository;

    @Test
    @DisplayName("앱 버전 지원 여부 변경")
    void success_1() {
        // given
        List<AppVersion> appVersionList = List.of(
                new AppVersion(OperationSystem.ANDROID, "1.0.0"),
                new AppVersion(OperationSystem.ANDROID, "1.2.0"),
                new AppVersion(OperationSystem.IOS, "2.0.0"),
                new AppVersion(OperationSystem.IOS, "2.0.1")
        );
        when(appVersionRepository.findAll())
                .thenReturn(appVersionList);

        Set<OSAndVersion> osAndVersions = Set.of(
                new OSAndVersion(OperationSystem.ANDROID, "1.0.1"),
                new OSAndVersion(OperationSystem.ANDROID, "1.2.0"),
                new OSAndVersion(OperationSystem.IOS, "2.0.2")
        );

        // when
        appVersionService.changeSupportedList(osAndVersions);

        // then
        ArgumentCaptor<AppVersion> deleteArgument = ArgumentCaptor.forClass(AppVersion.class);
        then(appVersionRepository).should(times(3)).delete(deleteArgument.capture());
        List<OSAndVersion> deleteList = deleteArgument.getAllValues().stream()
                .map(appVersion -> new OSAndVersion(appVersion.getOs(), appVersion.getVersion()))
                .toList();
        assertThat(deleteList).containsExactlyInAnyOrder(
                new OSAndVersion(OperationSystem.ANDROID, "1.0.0"),
                new OSAndVersion(OperationSystem.IOS, "2.0.0"),
                new OSAndVersion(OperationSystem.IOS, "2.0.1")
        );

        ArgumentCaptor<List<AppVersion>> saveArgument = ArgumentCaptor.forClass(List.class);
        then(appVersionRepository).should(times(1)).saveAll(saveArgument.capture());
        List<OSAndVersion> saveList = saveArgument.getValue().stream()
                .map(appVersion -> new OSAndVersion(appVersion.getOs(), appVersion.getVersion()))
                .toList();
        assertThat(saveList).containsExactlyInAnyOrder(
                new OSAndVersion(OperationSystem.ANDROID, "1.0.1"),
                new OSAndVersion(OperationSystem.IOS, "2.0.2")
        );

        then(appVersionRepository).shouldHaveNoMoreInteractions();
    }
}