package com.playkuround.playkuroundserver.domain.score.dto;

import lombok.Getter;

@Getter
public class RankData {

    private String email;
    private Integer score;

    public RankData(String email, Integer score) {
        this.email = email;
        this.score = score;
    }
}
