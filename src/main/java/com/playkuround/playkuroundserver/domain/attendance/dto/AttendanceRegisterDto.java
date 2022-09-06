package com.playkuround.playkuroundserver.domain.attendance.dto;

import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import lombok.Getter;
import lombok.Setter;

public class AttendanceRegisterDto {

    @Getter
    @Setter
    public static class Request {
        @Latitude
        private Double latitude;

        @Longitude
        private Double longitude;
    }

}
