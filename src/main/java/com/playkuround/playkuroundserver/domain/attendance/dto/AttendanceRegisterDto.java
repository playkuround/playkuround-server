package com.playkuround.playkuroundserver.domain.attendance.dto;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.validation.Latitude;
import com.playkuround.playkuroundserver.global.validation.Longitude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class AttendanceRegisterDto {

    @Getter
    @Setter
    public static class Request {
        @Latitude
        private Double latitude;

        @Longitude
        private Double longitude;
    }

    @Getter
    @Setter
    public static class Response {

        private List<BadgeInfo> newBadges = new ArrayList<>();

        private static class BadgeInfo {
            String name;
            String description;

            public BadgeInfo(String name, String description) {
                this.name = name;
                this.description = description;
            }
        }

        public void addBadge(BadgeType badgeType) {
            this.newBadges.add(new BadgeInfo(badgeType.name(), badgeType.getDescription()));
        }
    }

}
