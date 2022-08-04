package com.playkuround.playkuroundserver.domain.landmark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class RequestSaveAdventure {

    @NotBlank
    private Long landmarkId;

    @NotBlank
    private Double latitude;

    @NotBlank
    private Double longitude;

}
