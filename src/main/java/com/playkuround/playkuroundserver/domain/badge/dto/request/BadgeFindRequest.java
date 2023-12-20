package com.playkuround.playkuroundserver.domain.badge.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BadgeFindRequest {

    @Schema(description = "뱃지 이름", example = "ATTENDANCE_7", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "뱃지 설명", example = "7일 연속 출석", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Schema(description = "뱃지를 획득한 날짜, 시각", example = "2023-12-20 11:13:21", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    public static BadgeFindRequest from(Badge badge) {
        return new BadgeFindRequest(badge.getBadgeType().name(), badge.getBadgeType().getDescription(), badge.getCreatedAt());
    }
}
