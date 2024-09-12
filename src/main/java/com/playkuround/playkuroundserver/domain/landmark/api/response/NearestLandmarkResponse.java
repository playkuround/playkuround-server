package com.playkuround.playkuroundserver.domain.landmark.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.landmark.dto.NearestLandmark;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NearestLandmarkResponse {

    @Schema(description = "랜드마크 이름", example = "동물생명과학관", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "사용자와의 거리(단위:m)", example = "0.0949307305463114", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double distance;

    @Schema(description = "랜드마크 ID", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long landmarkId;

    private NearestLandmarkResponse(String name, Double distance, Long landmarkId) {
        this.name = name;
        this.distance = distance;
        this.landmarkId = landmarkId;
    }

    public static NearestLandmarkResponse from(NearestLandmark nearestLandmark) {
        if (nearestLandmark.getLandmarkId() != null) {
            return new NearestLandmarkResponse(nearestLandmark.getName(), nearestLandmark.getDistance(), nearestLandmark.getLandmarkId());
        }
        return new NearestLandmarkResponse();
    }
}
