package com.playkuround.playkuroundserver.domain.appversion.dao;

import com.playkuround.playkuroundserver.domain.appversion.domain.AppVersion;
import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    boolean existsByOsAndVersion(OperationSystem os, String version);
}
