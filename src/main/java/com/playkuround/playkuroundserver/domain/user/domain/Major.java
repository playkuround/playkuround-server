package com.playkuround.playkuroundserver.domain.user.domain;

import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;

@SuppressWarnings("NonAsciiCharacters")
public enum Major {

    // 문과대학
    국어국문학과(Collage.문과대학),
    영어영문학과(Collage.문과대학),
    중어중문학과(Collage.문과대학),
    철학과(Collage.문과대학),
    사학과(Collage.문과대학),
    지리학과(Collage.문과대학),
    미디어커뮤니케이션학과(Collage.문과대학),
    문화콘텐츠학과(Collage.문과대학),

    // 이과대학
    수학과(Collage.이과대학),
    물리학과(Collage.이과대학),
    화학과(Collage.이과대학),

    // 건축대학
    건축학부(Collage.건축대학),

    // 공과대학
    사회환경공학부(Collage.공과대학),
    기계항공공학부(Collage.공과대학),
    전기전자공학부(Collage.공과대학),
    화학공학부(Collage.공과대학),
    컴퓨터공학부(Collage.공과대학),
    산업경영공학부_산업공학과(Collage.공과대학),
    산업경영공학부_신산업융합학과(Collage.공과대학),
    생물공학과(Collage.공과대학),
    K뷰티산업융합학과(Collage.공과대학),

    // 사회과학대학
    정치외교학과(Collage.사회과학대학),
    경제학과(Collage.사회과학대학),
    행정학과(Collage.사회과학대학),
    국제무역학과(Collage.사회과학대학),
    응용통계학과(Collage.사회과학대학),
    융합인재학과(Collage.사회과학대학),
    글로벌비즈니스학과(Collage.사회과학대학),

    // 경영대학
    경영학과(Collage.경영대학),
    기술경영학과(Collage.경영대학),

    // 부동산과학원
    부동산학과(Collage.부동산과학원),

    // KU융합과학기술원
    미래에너지공학과(Collage.KU융합과학기술원),
    스마트운행체공학과(Collage.KU융합과학기술원),
    스마트ICT융합공학과(Collage.KU융합과학기술원),
    화장품공학과(Collage.KU융합과학기술원),
    줄기세포재생공학과(Collage.KU융합과학기술원),
    의생명공학과(Collage.KU융합과학기술원),
    시스템생명공학과(Collage.KU융합과학기술원),
    융합생명공학과(Collage.KU융합과학기술원),

    // 상허생명과학대학
    생명과학특성학과(Collage.상허생명과학대학),
    동물자원과학과(Collage.상허생명과학대학),
    식량자원과학과(Collage.상허생명과학대학),
    축산식품생명공학과(Collage.상허생명과학대학),
    식품유통공학과(Collage.상허생명과학대학),
    환경보건과학과(Collage.상허생명과학대학),
    산림조경학과(Collage.상허생명과학대학),

    // 수의과대학
    수의예과(Collage.수의과대학),
    수의학과(Collage.수의과대학),

    // 예술디자인대학
    커뮤니케이션디자인학과(Collage.예술디자인대학),
    산업디자인학과(Collage.예술디자인대학),
    의상디자인학과(Collage.예술디자인대학),
    리빙디자인학과(Collage.예술디자인대학),
    현대미술학과(Collage.예술디자인대학),
    영상영화학과(Collage.예술디자인대학),

    // 사범대학
    일어교육과(Collage.사범대학),
    수학교육과(Collage.사범대학),
    체육교육과(Collage.사범대학),
    음악교육과(Collage.사범대학),
    교육공학과(Collage.사범대학),
    영어교육과(Collage.사범대학),
    교직과(Collage.사범대학),
    ;
    // 상허교양대학
    //국제학부(Collage.상허교양대학);

    private final Collage collage;

    Major(Collage collage) {
        this.collage = collage;
    }

    public BadgeType getCollageBadgeType() {
        return collage.collageBadgeType;
    }

    enum Collage {
        문과대학(BadgeType.COLLEGE_OF_LIBERAL_ARTS),
        이과대학(BadgeType.COLLEGE_OF_SCIENCES),
        건축대학(BadgeType.COLLEGE_OF_ARCHITECTURE),
        공과대학(BadgeType.COLLEGE_OF_ENGINEERING),
        사회과학대학(BadgeType.COLLEGE_OF_SOCIAL_SCIENCES),
        경영대학(BadgeType.COLLEGE_OF_BUSINESS_ADMINISTRATION),
        부동산과학원(BadgeType.COLLEGE_OF_REAL_ESTATE),
        KU융합과학기술원(BadgeType.COLLEGE_OF_INSTITUTE_TECHNOLOGY),
        상허생명과학대학(BadgeType.COLLEGE_OF_BIOLOGICAL_SCIENCES),
        수의과대학(BadgeType.COLLEGE_OF_VETERINARY_MEDICINE),
        예술디자인대학(BadgeType.COLLEGE_OF_ART_AND_DESIGN),
        사범대학(BadgeType.COLLEGE_OF_EDUCATION),
        //상허교양대학
        ;

        private final BadgeType collageBadgeType;

        Collage(BadgeType collageBadgeType) {
            this.collageBadgeType = collageBadgeType;
        }
    }

}
