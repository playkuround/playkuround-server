package com.playkuround.playkuroundserver.domain.badge.application;

import com.playkuround.playkuroundserver.domain.badge.application.college.CollegeBadge;
import com.playkuround.playkuroundserver.domain.badge.application.college.CollegeBadgeList;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CollegeBadgeTest {

    @Test
    @DisplayName("뱃지 개수가 0개이면 빈리스트가 반환된다")
    void CollegeBadge() {
        Map<LandmarkType, BadgeType> map = Map.ofEntries(
                Map.entry(LandmarkType.인문학관, BadgeType.COLLEGE_OF_LIBERAL_ARTS),
                Map.entry(LandmarkType.과학관, BadgeType.COLLEGE_OF_SCIENCES),
                Map.entry(LandmarkType.건축관, BadgeType.COLLEGE_OF_ARCHITECTURE),
                Map.entry(LandmarkType.신공학관, BadgeType.COLLEGE_OF_ENGINEERING),
                Map.entry(LandmarkType.상허연구관, BadgeType.COLLEGE_OF_SOCIAL_SCIENCES),
                Map.entry(LandmarkType.경영관, BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION),
                Map.entry(LandmarkType.부동산학관, BadgeType.COLLEGE_OF_REAL_ESTATE),
                Map.entry(LandmarkType.생명과학관, BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY),
                Map.entry(LandmarkType.동물생명과학관, BadgeType.COLLEGE_OF_BIOLOGICAL_SCIENCES),
                Map.entry(LandmarkType.수의학관, BadgeType.COLLEGE_OF_VETERINARY_MEDICINE),
                Map.entry(LandmarkType.예디대, BadgeType.COLLEGE_OF_ART_AND_DESIGN),
                Map.entry(LandmarkType.공예관, BadgeType.COLLEGE_OF_ART_AND_DESIGN),
                Map.entry(LandmarkType.교육과학관, BadgeType.COLLEGE_OF_EDUCATION)
        );

        LandmarkType[] landmarkTypes = LandmarkType.values();

        List<CollegeBadge> collegeBadges = CollegeBadgeList.getCollegeBadges();
        for (LandmarkType landmarkType : landmarkTypes) {
            if (landmarkType == LandmarkType.공학관A ||
                    landmarkType == LandmarkType.공학관B ||
                    landmarkType == LandmarkType.공학관C) {
                continue;
            }

            int flag = 0;

            for (CollegeBadge collegeBadge : collegeBadges) {
                if (collegeBadge.supports(landmarkType)) {
                    BadgeType badge = collegeBadge.getBadge();
                    assertThat(badge).isEqualTo(map.get(landmarkType));
                    flag++;
                }
            }
            if (flag == 0) {
                assertThat(map.get(landmarkType)).isNull();
            }
            else if (flag > 1) {
                throw new IllegalStateException("뱃지가 2개 이상입니다.");
            }
        }

        // 공학관 A, B, C는 해당 뱃지가 2개이다.
        int a = 0, b = 0, c = 0;
        BadgeType[] badgeTypes = new BadgeType[6];

        for (CollegeBadge collegeBadge : collegeBadges) {
            if (collegeBadge.supports(LandmarkType.공학관A)) {
                badgeTypes[a] = collegeBadge.getBadge();
                a++;
            }
            if (collegeBadge.supports(LandmarkType.공학관B)) {
                badgeTypes[2 + b] = collegeBadge.getBadge();
                b++;
            }
            if (collegeBadge.supports(LandmarkType.공학관C)) {
                badgeTypes[4 + c] = collegeBadge.getBadge();
                c++;
            }
        }
        for (int i = 0; i < 3; i++) {
            boolean result = (badgeTypes[i * 2] == BadgeType.COLLEGE_OF_ENGINEERING &&
                    badgeTypes[i * 2 + 1] == BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY) ||
                    (badgeTypes[i * 2] == BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY &&
                            badgeTypes[i * 2 + 1] == BadgeType.COLLEGE_OF_ENGINEERING);
            assertThat(result).isTrue();
        }

    }

}