package com.playkuround.playkuroundserver.domain.event.api.response;

import com.playkuround.playkuroundserver.domain.event.domain.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EventResponse {

    @Schema(description = "event id", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private long id;

    @Schema(description = "이벤트 이름", example = "경영X건축 대전", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "이미지 URL", example = "https://www.asdf.com/image")
    private String imageUrl;

    @Schema(description = "내용", example = "경영과 건축의 대결이 펼쳐집니다!")
    private String description;

    @Schema(description = "연결된 링크", example = "https://www.asdf.com/link")
    private String referenceUrl;

    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getTitle(), event.getImageUrl(), event.getDescription(), event.getReferenceUrl());
    }
}
