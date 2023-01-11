package com.playkuround.playkuroundserver.domain.badge.dto;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.global.validation.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BadgeSaveDto {

    @ValidEnum(enumClass = BadgeType.class, message = "잘못된 BadgeType 입니다.")
    private String badgeType;
}
