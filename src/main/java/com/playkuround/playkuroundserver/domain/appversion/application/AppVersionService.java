package com.playkuround.playkuroundserver.domain.appversion.application;

import com.playkuround.playkuroundserver.domain.appversion.dao.AppVersionRepository;
import com.playkuround.playkuroundserver.domain.appversion.domain.AppVersion;
import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.domain.appversion.dto.OSAndVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppVersionService {

    private final AppVersionRepository appVersionRepository;

    public boolean isSupportedVersion(OperationSystem os, String version) {
        return appVersionRepository.existsByOsAndVersion(os, version);
    }

    @Transactional
    public void changeSupportedList(Set<OSAndVersion> osAndVersions) {
        Set<OSAndVersion> osAndVersionHashSet = new HashSet<>(osAndVersions);

        List<AppVersion> currentSupportedAppVersions = appVersionRepository.findAll();
        for (AppVersion appVersion : currentSupportedAppVersions) {
            OSAndVersion osAndVersion = new OSAndVersion(appVersion.getOs(), appVersion.getVersion());
            if (!osAndVersionHashSet.remove(osAndVersion)) {
                appVersionRepository.delete(appVersion);
            }
        }

        List<AppVersion> newAppVersions = osAndVersionHashSet.stream()
                .map(osAndVersion -> new AppVersion(osAndVersion.os(), osAndVersion.version()))
                .toList();
        appVersionRepository.saveAll(newAppVersions);
    }
}
