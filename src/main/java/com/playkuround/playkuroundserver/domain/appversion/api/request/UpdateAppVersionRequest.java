package com.playkuround.playkuroundserver.domain.appversion.api.request;

import com.playkuround.playkuroundserver.domain.appversion.domain.OperationSystem;
import com.playkuround.playkuroundserver.global.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
        @Schema(description = "OS(ANDROID 또는 IOS)", example = "ANDROID", requiredMode = Schema.RequiredMode.REQUIRED)
        private String os;

        @NotBlank
        @Schema(description = "지원 버전", example = "2.0.3", requiredMode = Schema.RequiredMode.REQUIRED)
        private String version;
    }
}
