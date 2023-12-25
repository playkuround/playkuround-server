package com.playkuround.playkuroundserver.domain.adventure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyScore {
    private final Integer score;
    private final Integer rank;
}
