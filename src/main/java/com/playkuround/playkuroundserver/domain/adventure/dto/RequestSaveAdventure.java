package com.playkuround.playkuroundserver.domain.adventure.dto;

import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
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

    @Latitude
    private Double latitude;

    @Longitude
    private Double longitude;

}
