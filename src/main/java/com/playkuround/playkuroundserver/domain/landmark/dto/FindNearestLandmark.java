package com.playkuround.playkuroundserver.domain.landmark.dto;

import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import lombok.*;

public class FindNearestLandmark {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @Latitude
        private Double latitude;

        @Longitude
        private Double longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Long id;
        private String name;
        private double distance;
        private String gameType;

        public static FindNearestLandmark.Response of(Landmark landmark, double distance) {
            return Response.builder()
                    .id(landmark.getId())
                    .name(landmark.getName())
                    .distance(distance)
                    .gameType(landmark.getGameType().name())
                    .build();
        }

    }
}
