package com.playkuround.playkuroundserver.domain.systemcheck.application;

import com.playkuround.playkuroundserver.domain.appversion.dao.AppVersionRepository;
import com.playkuround.playkuroundserver.domain.appversion.domain.AppVersion;
import com.playkuround.playkuroundserver.domain.appversion.dto.OSAndVersion;
import com.playkuround.playkuroundserver.domain.systemcheck.dao.SystemCheckRepository;
import com.playkuround.playkuroundserver.domain.systemcheck.domain.SystemCheck;
import com.playkuround.playkuroundserver.domain.systemcheck.dto.HealthCheckDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemCheckService {

    private final AppVersionRepository appVersionRepository;
    private final SystemCheckRepository systemCheckRepository;

    public boolean isSystemAvailable() {
        Optional<SystemCheck> optionalSystemCheck = systemCheckRepository.findTopByOrderByUpdatedAtDesc();
        return optionalSystemCheck.map(SystemCheck::isAvailable)
                .orElse(false);
    }

    @Transactional
    public void changeSystemAvailable(boolean available) {
        systemCheckRepository.deleteAll();
        SystemCheck systemCheck = new SystemCheck(available);
        systemCheckRepository.save(systemCheck);
    }

    public HealthCheckDto healthCheck() {
        boolean systemAvailable = isSystemAvailable();
        List<AppVersion> supportAppVersionList = appVersionRepository.findAll();

        List<OSAndVersion> list = supportAppVersionList.stream()
                .map(appVersion -> new OSAndVersion(appVersion.getOs(), appVersion.getVersion()))
                .distinct()
                .toList();

        return new HealthCheckDto(systemAvailable, list);
    }
}
