package com.playkuround.playkuroundserver.domain.landmark.dto;

import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import lombok.Getter;

@Getter
public class NearestLandmark {

    private String name;
    private Long landmarkId;
    private double distance;
    private boolean hasResult = false;

    public void update(Landmark landmark, double distance) {
        this.hasResult = true;
        if (landmarkId == null || this.distance > distance) {
            this.distance = distance;
            this.landmarkId = landmark.getId();
            this.name = landmark.getName().name();
        }
    }
}
