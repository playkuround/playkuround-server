package com.playkuround.playkuroundserver.domain.adventure.dto.request;

import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdventureSaveRequest {

    @NotNull(message = "랜드마크id는 필수입니다.")
    @Schema(description = "랜드마크ID", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long landmarkId;

    @Latitude
    @Schema(description = "위도", example = "37.1413", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;

    @Longitude
    @Schema(description = "경도", example = "130.1413", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;

    @NotNull(message = "점수는 필수입니다.")
    @Schema(description = "점수", example = "110", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer score;

    @NotBlank(message = "점수 타입은 필수입니다.")
    @Schema(description = "점수타입(별도문서 참고)", example = "QUIZ", requiredMode = Schema.RequiredMode.REQUIRED)
    private String scoreType;
}