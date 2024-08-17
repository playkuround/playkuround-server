package com.playkuround.playkuroundserver.domain.badge.api.request;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.validation.ValidEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class RepresentationBadgeRequest {

    @ValidEnum(enumClass = BadgeType.class, message = "잘못된 badge type 입니다.")
    @Schema(description = "설정할 뱃지 타입", example = "ATTENDANCE_CHILDREN_DAY", requiredMode = Schema.RequiredMode.REQUIRED)
    private String badgeType;

}
