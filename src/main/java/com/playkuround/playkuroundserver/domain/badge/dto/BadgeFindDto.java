package com.playkuround.playkuroundserver.domain.badge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playkuround.playkuroundserver.domain.badge.domain.Badge;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BadgeFindDto {

    private String name;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Builder
    public BadgeFindDto(String name, String description, LocalDateTime createdAt) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static BadgeFindDto of(Badge badge) {
        return BadgeFindDto.builder()
                .createdAt(badge.getCreatedAt())
                .name(badge.getBadgeType().name())
                .description(badge.getBadgeType().getDescription())
                .build();
    }
}
