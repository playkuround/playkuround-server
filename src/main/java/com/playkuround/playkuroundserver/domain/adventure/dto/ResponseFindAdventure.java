package com.playkuround.playkuroundserver.domain.adventure.dto;

import com.playkuround.playkuroundserver.domain.adventure.domain.Adventure;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseFindAdventure {

    private Long landmarkId;
    private LocalDateTime visitedDateTime;

    @Builder
    public ResponseFindAdventure(Long landmarkId, LocalDateTime visitedDateTime) {
        this.landmarkId = landmarkId;
        this.visitedDateTime = visitedDateTime;
    }

    public static ResponseFindAdventure of(Adventure adventure) {
        return ResponseFindAdventure.builder()
                .landmarkId(adventure.getLandmark().getId())
                .visitedDateTime(adventure.getCreateAt())
                .build();
    }
}
