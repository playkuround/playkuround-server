package com.playkuround.playkuroundserver.domain.landmark.dto;

import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import lombok.Getter;

@Getter
public class NearestLandmark {

    private String name;
    private Long landmarkId;
    private double distance;

    public void update(Landmark landmark, double distance) {
        if (this.landmarkId == null || this.distance > distance) {
            this.distance = distance;
            this.landmarkId = landmark.getId();
            this.name = landmark.getName().name();
        }
    }
}
