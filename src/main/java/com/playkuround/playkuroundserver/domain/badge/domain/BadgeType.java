package com.playkuround.playkuroundserver.domain.badge.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BadgeType {

    // 출석 체크
    ATTENDANCE_1("첫 출석"),
    ATTENDANCE_5("5회 출석"),
    ATTENDANCE_10("10회 출석"),
    ATTENDANCE_30("30회 출석"),
    ATTENDANCE_50("50회 출석"),
    ATTENDANCE_100("100회 출석"),

    // 기념일
    ATTENDANCE_FOUNDATION_DAY("05월 15일 개교 기념일에 출석"),
    ATTENDANCE_ARBOR_DAY("04월 05일 식목일에 출석"),
    ATTENDANCE_CHILDREN_DAY("05월 05일 어린이날에 출석"),
    ATTENDANCE_WHITE_DAY("03월 14일 화이트데이에 출석"),
    ATTENDANCE_DUCK_DAY("05월 02일 오리데이에 출석"),

    // 대학별
    COLLEGE_OF_LIBERAL_ARTS("문과대학 1회 이상 탐험"),
    COLLEGE_OF_SCIENCES("이과대학 1회 이상 탐험"),
    COLLEGE_OF_ARCHITECTURE("건축대학 1회 이상 탐험"),
    COLLEGE_OF_ENGINEERING("공과대학 1회 이상 탐험"),
    COLLEGE_OF_SOCIAL_SCIENCES("사회과학대학 1회 이상 탐험"),
    COLLEGE_OF_BUSINESS_ADMINISTRATION("경영대학 1회 이상 탐험"),
    COLLEGE_OF_REAL_ESTATE("부동산과학원 1회 이상 탐험"),
    COLLEGE_OF_INSTITUTE_TECHNOLOGY("융합과학기술원 1회 이상 탐험"),
    COLLEGE_OF_BIOLOGICAL_SCIENCES("생명과학대학 1회 이상 탐험"),
    COLLEGE_OF_VETERINARY_MEDICINE("수의과대학 1회 이상 탐험"),
    COLLEGE_OF_ART_AND_DESIGN("예술디자인대학 1회 이상 탐험"),
    COLLEGE_OF_EDUCATION("사범대학 1회 이상 탐험"),

    // 경영대 특별
    COLLEGE_OF_BUSINESS_ADMINISTRATION_10("경영대학 10회 이상 탐험"),
    COLLEGE_OF_BUSINESS_ADMINISTRATION_30("경영대학 30회 이상 탐험"),
    COLLEGE_OF_BUSINESS_ADMINISTRATION_50("경영대학 50회 이상 탐험"),
    COLLEGE_OF_BUSINESS_ADMINISTRATION_70("경영대학 70회 이상 탐험"),
    COLLEGE_OF_BUSINESS_ADMINISTRATION_100_AND_FIRST_PLACE("경영대학 100회 이상 탐험 및 1등 달성"), // 수동

    // 예디대 특별
    COLLEGE_OF_ART_AND_DESIGN_BEFORE_NOON("예술디자인대학 09:00 ~ 12:00 탐험"),
    COLLEGE_OF_ART_AND_DESIGN_AFTER_NOON("예술디자인대학 12:00 ~ 18:00 탐험"),
    COLLEGE_OF_ART_AND_DESIGN_NIGHT("예술디자인대학 23:00 ~ 04:00 탐험"),

    // 공대 특별
    COLLEGE_OF_ENGINEERING_A("공대 A동 10회 이상 탐험"),
    COLLEGE_OF_ENGINEERING_B("공대 B동 10회 이상 탐험"),
    COLLEGE_OF_ENGINEERING_C("공대 C동 10회 이상 탐험"),

    // 스토리용
    THE_DREAM_OF_DUCK("스토리 컷씬 마스터"),

    // 월간랭킹
    MONTHLY_RANKING_1("월간 랭킹 1등"), // 수동
    MONTHLY_RANKING_2("월간 랭킹 2등"), // 수동
    MONTHLY_RANKING_3("월간 랭킹 3등"), // 수동

    ;

    private final String description;
}
