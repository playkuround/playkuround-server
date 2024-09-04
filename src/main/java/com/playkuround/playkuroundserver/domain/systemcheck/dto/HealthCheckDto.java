package com.playkuround.playkuroundserver.domain.systemcheck.dto;

import com.playkuround.playkuroundserver.domain.appversion.dto.OSAndVersion;

import java.util.List;

public record HealthCheckDto(boolean systemAvailable, List<OSAndVersion> supportAppVersionList) {
}
