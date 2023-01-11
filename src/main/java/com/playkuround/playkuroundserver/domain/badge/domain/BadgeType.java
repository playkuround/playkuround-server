package com.playkuround.playkuroundserver.domain.badge.domain;

import com.playkuround.playkuroundserver.domain.badge.exception.BadgeTypeNotFoundException;
import com.playkuround.playkuroundserver.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BadgeType {

    ATTENDANCE_1("첫 출석"),
    ATTENDANCE_3("3일 연속 출석"),
    ATTENDANCE_7("7일 연속 출석"),
    ATTENDANCE_30("30일 연속 출석"),
    ATTENDANCE_100("100일 연속 출석"),
    ATTENDANCE_FOUNDATION_DAY("개교 기념일"),

    ADVENTURE_1("첫 탐험"),
    ADVENTURE_5("탐험 5번 이상"),
    ADVENTURE_10("탐험 10번 이상"),
    ADVENTURE_30("탐험 30번 이상"),

    ENGINEER("공대생"), // 공대 건물(A, B, C, 신공, 이과대) 모두 탐험
    ARTIST("예술가"), // 예디대, 공예관 탐험
    CEO("CEO"), // 경영관 경제학관 탐험
    NATIONAL_PLAYER("국가대표"), // 체육시설, 운동장, 실내 체육관
    CONQUEROR("정복자"), // 랜드마크 모두 탐험
    NEIL_ARMSTRONG("닐 암스트롱") // 문 모두 탐험
    ;

    private final String description;

    public String getDescription() {
        return description;
    }

    public static BadgeType findBadgeTypeByLandmarkId(Long landmarkId) {
        if (22 <= landmarkId && landmarkId <= 26) return ENGINEER;
        if (landmarkId == 8 || landmarkId == 28) return ARTIST;
        if (landmarkId == 15) return CEO;
        if (landmarkId == 37 || landmarkId == 38) return NATIONAL_PLAYER;
        if (39 <= landmarkId && landmarkId <= 44) return NEIL_ARMSTRONG;
        throw new BadgeTypeNotFoundException(ErrorCode.INVALID_Badge_TYPE);
    }

    public static Long requiredAdventureCountForBadge(BadgeType badgeType) {
        if (badgeType == ENGINEER) return 5L;
        if (badgeType == ARTIST) return 2L;
        if (badgeType == CEO) return 1L;
        if (badgeType == NATIONAL_PLAYER) return 2L;
        if (badgeType == NEIL_ARMSTRONG) return 6L;
        throw new BadgeTypeNotFoundException(ErrorCode.INVALID_Badge_TYPE);
    }

    public static BadgeType findBadgeTypeByAdventureCount(Long count) {
        if (count == 1) return BadgeType.ADVENTURE_1;
        else if (count == 5) return BadgeType.ADVENTURE_5;
        else if (count == 10) return BadgeType.ADVENTURE_10;
        else if (count == 30) return BadgeType.ADVENTURE_30;
        else if (count == 44) return BadgeType.CONQUEROR;
        throw new BadgeTypeNotFoundException(ErrorCode.INVALID_Badge_TYPE);
    }
}
