package com.playkuround.playkuroundserver.domain.landmark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class RequestSaveAdventure {

    @NotNull
    private Long landmarkId;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

}
