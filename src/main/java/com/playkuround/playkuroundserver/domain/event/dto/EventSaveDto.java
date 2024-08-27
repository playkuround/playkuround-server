package com.playkuround.playkuroundserver.domain.event.dto;

public record EventSaveDto(String title,
                           String imageUrl,
                           String description,
                           String referenceUrl,
                           boolean display) {
}
