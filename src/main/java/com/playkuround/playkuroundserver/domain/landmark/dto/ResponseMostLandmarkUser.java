package com.playkuround.playkuroundserver.domain.landmark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ResponseMostLandmarkUser {

    private String nickname;
    private Integer count;
    private String message;
    private Long userId;

    @Builder
    public ResponseMostLandmarkUser(String nickname, Integer count, String message, Long userId) {
        this.nickname = nickname;
        this.count = count;
        this.message = message;
        this.userId = userId;
    }
}
