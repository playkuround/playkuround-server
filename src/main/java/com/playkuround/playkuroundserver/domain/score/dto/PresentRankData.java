package com.playkuround.playkuroundserver.domain.score.dto;

import lombok.Getter;

@Getter
public class PresentRankData {

    private String email;
    private Integer score;

    public PresentRankData(String email, Integer score) {
        this.email = email;
        this.score = score;
    }
}
