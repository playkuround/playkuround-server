package com.playkuround.playkuroundserver.domain.systemcheck.api.response;

import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.domain.appversion.dto.OSAndVersion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HealthCheckResponse {

    private boolean systemAvailable;
    private List<OSAndVersions> supportAppVersionList;

    public HealthCheckResponse(boolean systemAvailable, List<OSAndVersion> osAndVersions) {
        this.systemAvailable = systemAvailable;
        this.supportAppVersionList = new ArrayList<>();

        OperationSystem[] osArray = OperationSystem.values();
        for (OperationSystem os : osArray) {
            List<String> supportVersion = osAndVersions.stream()
                    .filter(osAndVersion -> osAndVersion.os().equals(os))
                    .map(OSAndVersion::version)
                    .toList();
            supportAppVersionList.add(new OSAndVersions(os, supportVersion));
        }
    }

    @Getter
    @NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class OSAndVersions {
        private String os;
        private List<String> version;

        public OSAndVersions(OperationSystem os, List<String> version) {
            this.os = os.name();
            this.version = version;
        }
    }
}
