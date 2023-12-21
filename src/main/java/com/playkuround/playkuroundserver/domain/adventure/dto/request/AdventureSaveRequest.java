package com.playkuround.playkuroundserver.domain.adventure.dto.request;

import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
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
    private Long landmarkId;

    @Latitude
    private Double latitude;

    @Longitude
    private Double longitude;

    @NotNull(message = "점수는 필수입니다.")
    private Integer score;

    @NotBlank(message = "점수 타입은 필수입니다.")
    private String scoreType;
}
