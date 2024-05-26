package com.playkuround.playkuroundserver.domain.appversion.api.request;

import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.global.validation.ValidEnum;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UpdateAppVersionRequest {

    @Valid
    private List<OsAndVersion> osAndVersions;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class OsAndVersion {
        @ValidEnum(enumClass = OperationSystem.class, message = "잘못된 OS입니다.")
        private String os;

        private String version;
    }
}
