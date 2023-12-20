package com.playkuround.playkuroundserver.domain.badge.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BadgeFindRequest {

    private String name;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static BadgeFindRequest from(Badge badge) {
        return new BadgeFindRequest(badge.getBadgeType().name(), badge.getBadgeType().getDescription(), badge.getCreatedAt());
    }
}
