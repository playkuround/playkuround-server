package com.playkuround.playkuroundserver.domain.attendance.dto;

import lombok.Getter;
import lombok.Setter;

public class AttendanceRegisterDto {

    @Getter
    @Setter
    public static class Request {
        private double latitude;
        private double longitude;
    }

}
