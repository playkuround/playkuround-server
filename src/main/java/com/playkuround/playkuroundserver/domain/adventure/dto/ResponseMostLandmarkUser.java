package com.playkuround.playkuroundserver.domain.adventure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
