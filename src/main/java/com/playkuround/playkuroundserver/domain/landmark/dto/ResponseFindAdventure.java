package com.playkuround.playkuroundserver.domain.landmark.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseFindAdventure {

    private Long landmarkId;
    private LocalDateTime createdDateTime;

    @Builder
    public ResponseFindAdventure(Long landmarkId, LocalDateTime createdDateTime) {
        this.landmarkId = landmarkId;
        this.createdDateTime = createdDateTime;
    }
}
