package com.playkuround.playkuroundserver.domain.landmark.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FindNearLandmarkResponse {

    @Schema(description = "랜드마크 이름", example = "동물생명과학관", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "사용자와의 거리(단위:m)", example = "0.0949307305463114", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double distance;

    @Schema(description = "랜드마크 ID", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long landmarkId;

    private FindNearLandmarkResponse() {
    }

    public static FindNearLandmarkResponse createEmptyResponse() {
        FindNearLandmarkResponse response = new FindNearLandmarkResponse();
        response.landmarkId = null;
        response.name = null;
        response.distance = null;
        return response;
    }

    public void update(Landmark landmark, double distance) {
        if (landmarkId == null || this.distance > distance) {
            this.distance = distance;
            this.name = landmark.getName();
            this.landmarkId = landmark.getId();
        }
    }
}
