package com.playkuround.playkuroundserver.domain.landmark.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ResponseMostLandmarkUser {

    private String nickname;
    private Integer count;

    @Builder
    public ResponseMostLandmarkUser(String nickname, Integer count) {
        this.nickname = nickname;
        this.count = count;
    }
}
