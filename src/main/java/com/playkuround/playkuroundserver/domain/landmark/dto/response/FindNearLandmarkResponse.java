package com.playkuround.playkuroundserver.domain.landmark.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import lombok.Getter;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FindNearLandmarkResponse {

    private Long landmarkId;
    private String name;
    private Double distance;

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
