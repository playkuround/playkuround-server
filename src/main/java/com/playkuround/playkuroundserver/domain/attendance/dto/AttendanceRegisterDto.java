package com.playkuround.playkuroundserver.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class AttendanceRegisterDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        @Latitude
        private Double latitude;

        @Longitude
        private Double longitude;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        private List<BadgeInfo> newBadges = new ArrayList<>();

        @NoArgsConstructor
        @AllArgsConstructor
        private static class BadgeInfo {
            @JsonProperty("name")
            String name;

            @JsonProperty("description")
            String description;
        }

        public void addBadge(BadgeType badgeType) {
            this.newBadges.add(new BadgeInfo(badgeType.name(), badgeType.getDescription()));
        }
    }

}
