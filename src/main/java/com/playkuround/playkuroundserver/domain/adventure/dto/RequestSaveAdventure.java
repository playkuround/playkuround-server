package com.playkuround.playkuroundserver.domain.adventure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestSaveAdventure {

    @NotNull(message = "랜드마크id는 필수입니다.")
    private Long landmarkId;

    @NotNull(message = "위도(latitude)는 필수입니다.")
    private Double latitude;

    @NotNull(message = "경도(longitude)는 필수입니다.")
    private Double longitude;

}
