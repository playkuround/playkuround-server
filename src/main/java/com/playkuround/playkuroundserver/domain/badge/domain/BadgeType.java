package com.playkuround.playkuroundserver.domain.badge.domain;

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
}
