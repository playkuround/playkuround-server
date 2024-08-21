package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.domain.badge.application.college.CollegeBadge;
import com.playkuround.playkuroundserver.domain.badge.application.college.CollegeBadgeList;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CollegeBadgeTest {

    // 기획에서 정한 건물별 뱃지
    private final List<CollegeBadgeRecord> answer = List.of(
            new CollegeBadgeRecord(LandmarkType.인문학관, BadgeType.COLLEGE_OF_LIBERAL_ARTS),
            new CollegeBadgeRecord(LandmarkType.과학관, BadgeType.COLLEGE_OF_SCIENCES),
            new CollegeBadgeRecord(LandmarkType.건축관, BadgeType.COLLEGE_OF_ARCHITECTURE),
            new CollegeBadgeRecord(LandmarkType.신공학관, BadgeType.COLLEGE_OF_ENGINEERING),
            new CollegeBadgeRecord(LandmarkType.상허연구관, BadgeType.COLLEGE_OF_SOCIAL_SCIENCES),
            new CollegeBadgeRecord(LandmarkType.경영관, BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION),
            new CollegeBadgeRecord(LandmarkType.부동산학관, BadgeType.COLLEGE_OF_REAL_ESTATE),
            new CollegeBadgeRecord(LandmarkType.생명과학관, BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY),
            new CollegeBadgeRecord(LandmarkType.동물생명과학관, BadgeType.COLLEGE_OF_BIOLOGICAL_SCIENCES),
            new CollegeBadgeRecord(LandmarkType.수의학관, BadgeType.COLLEGE_OF_VETERINARY_MEDICINE),
            new CollegeBadgeRecord(LandmarkType.예디대, BadgeType.COLLEGE_OF_ART_AND_DESIGN),
            new CollegeBadgeRecord(LandmarkType.공예관, BadgeType.COLLEGE_OF_ART_AND_DESIGN),
            new CollegeBadgeRecord(LandmarkType.교육과학관, BadgeType.COLLEGE_OF_EDUCATION),
            new CollegeBadgeRecord(LandmarkType.산학협동관, BadgeType.COLLEGE_OF_SANG_HUH),
            new CollegeBadgeRecord(LandmarkType.법학관, BadgeType.COLLEGE_OF_INTERNATIONAL),
            new CollegeBadgeRecord(LandmarkType.공학관A, BadgeType.COLLEGE_OF_ENGINEERING),
            new CollegeBadgeRecord(LandmarkType.공학관A, BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY),
            new CollegeBadgeRecord(LandmarkType.공학관B, BadgeType.COLLEGE_OF_ENGINEERING),
            new CollegeBadgeRecord(LandmarkType.공학관B, BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY),
            new CollegeBadgeRecord(LandmarkType.공학관C, BadgeType.COLLEGE_OF_ENGINEERING),
            new CollegeBadgeRecord(LandmarkType.공학관C, BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY)
    );

    @ParameterizedTest
    @DisplayName("랜드마크 탐험 뱃지 테스트")
    @EnumSource(value = LandmarkType.class)
    void collegeBadge(LandmarkType landmarkType) {
        List<BadgeType> answerBadeType = answer.stream()
                .filter(collegeBadgeRecord -> collegeBadgeRecord.landmarkType() == landmarkType)
                .map(CollegeBadgeRecord::badgeType)
                .toList();

        List<BadgeType> badgeTypes = CollegeBadgeList.getCollegeBadges().stream()
                .filter(collegeBadge -> collegeBadge.supports(landmarkType))
                .map(CollegeBadge::getBadge)
                .toList();

        assertThat(badgeTypes).containsExactlyInAnyOrderElementsOf(answerBadeType);
    }

    private record CollegeBadgeRecord(LandmarkType landmarkType, BadgeType badgeType) {
    }

}