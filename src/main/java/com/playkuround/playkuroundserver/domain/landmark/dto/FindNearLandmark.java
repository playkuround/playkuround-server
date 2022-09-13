package com.playkuround.playkuroundserver.domain.landmark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import lombok.*;

public class FindNearLandmark {

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
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Response {

        private Long id;
        private String name;
        private Double distance;
        private String gameType;

        public static FindNearLandmark.Response of(Landmark landmark, double distance) {
            return Response.builder()
                    .id(landmark.getId())
                    .name(landmark.getName())
                    .distance(distance)
                    .gameType(landmark.getGameType().name())
                    .build();

        }

        public static FindNearLandmark.Response of() {
            return new FindNearLandmark.Response();
        }
    }
}
