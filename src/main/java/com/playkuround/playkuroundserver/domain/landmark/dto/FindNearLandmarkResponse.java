package com.playkuround.playkuroundserver.domain.landmark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FindNearLandmarkResponse {

    private Long landmarkId;
    private String name;
    private Double distance;

    public static FindNearLandmarkResponse createEmptyResponse() {
        FindNearLandmarkResponse response = new FindNearLandmarkResponse();
        response.landmarkId = null;
        response.name = null;
        response.distance = null;
        return response;
    }

    public void update(Landmark landmark, double distance) {
        if (landmarkId == null || this.distance > distance) {

            this.landmarkId = landmark.getId();
            this.name = landmark.getName();
            this.distance = distance;
        }
    }
}
