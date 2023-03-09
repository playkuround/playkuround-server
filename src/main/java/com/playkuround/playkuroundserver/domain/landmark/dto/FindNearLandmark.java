package com.playkuround.playkuroundserver.domain.landmark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FindNearLandmark {

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
        private Integer radius;

        public static FindNearLandmark.Response of(Landmark landmark, double distance) {
            return Response.builder()
                    .id(landmark.getId())
                    .name(landmark.getName())
                    .distance(distance)
                    .radius(landmark.getRecognitionRadius())
                    .gameType(landmark.getGameType().name())
                    .build();

        }

        public static FindNearLandmark.Response of() {
            return new FindNearLandmark.Response();
        }
    }
}
