package com.playkuround.playkuroundserver.domain.attendance.dto.request;

import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class AttendanceRegisterRequest {
    @Latitude
    private Double latitude;

    @Longitude
    private Double longitude;
}
