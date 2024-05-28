package com.playkuround.playkuroundserver.domain.appversion.dto;

import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;

public record OSAndVersion(OperationSystem os, String version) {
}
