package com.playkuround.playkuroundserver.domain.adventure.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseFindAdventure {

    private Long landmarkId;
    private LocalDateTime visitedDateTime;

    @Builder
    public ResponseFindAdventure(Long landmarkId, LocalDateTime visitedDateTime) {
        this.landmarkId = landmarkId;
        this.visitedDateTime = visitedDateTime;
    }
}
