package com.playkuround.playkuroundserver.domain.event.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class EventSaveRequest {

    @NotNull
    @Schema(description = "event title", example = "경영X건축 대전", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "이미지 URL", example = "https://www.asdf.com/image")
    private String imageUrl;

    @Schema(description = "내용", example = "경영과 건축의 대결이 펼쳐집니다!")
    private String description;

    @Schema(description = "연결된 링크", example = "https://www.asdf.com/link")
    private String referenceUrl;

    @Schema(description = "화면 노출 여부", example = "true", defaultValue = "false")
    private boolean display;

}
