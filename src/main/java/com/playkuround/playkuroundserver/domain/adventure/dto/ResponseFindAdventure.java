package com.playkuround.playkuroundserver.domain.adventure.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseFindAdventure {

    private Long landmarkId;

    public ResponseFindAdventure(Long landmarkId) {
        this.landmarkId = landmarkId;
    }

    public static ResponseFindAdventure of(Long landmarkId) {
        return new ResponseFindAdventure(landmarkId);
    }
}
