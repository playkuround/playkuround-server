package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LandmarkInitDb {
    /**
     * 임시 데이터 insert
     */
    private final double latitude = 0d;
    private final double longitude = 0d;
    private final LandmarkRepository landmarkRepository;

    public LandmarkInitDb(LandmarkRepository landmarkRepository) {
        this.landmarkRepository = landmarkRepository;
    }

    @EventListener(ApplicationReadyEvent.class) // 완전히 스프링이 뜬 후에 실행
    @Transactional
    public void initV2() {
        String[] landmarkNames = getLandmarkNames();
        for (String landmarkName : landmarkNames) {
            landmarkRepository.save(new Landmark(landmarkName, latitude, longitude));
        }
    }

    private String[] getLandmarkNames() {
        String[] landmarkNames = new String[44];
        landmarkNames[0] = "산학협동관";
        landmarkNames[1] = "입학정보관";
        landmarkNames[2] = "수의학관";
        landmarkNames[3] = "동물생명과학관";
        landmarkNames[4] = "생명과학관";
        landmarkNames[5] = "상허도서관";
        landmarkNames[6] = "의생명과학연구관";
        landmarkNames[7] = "예디대";
        landmarkNames[8] = "언어교육원";
        landmarkNames[9] = "법학관";
        landmarkNames[10] = "상허박물관";
        landmarkNames[11] = "행정관";
        landmarkNames[12] = "교육과학관(사범대)";
        landmarkNames[13] = "상허연구관";
        landmarkNames[14] = "경영관";
        landmarkNames[15] = "새천년관";
        landmarkNames[16] = "건축관";
        landmarkNames[17] = "부동산학관";
        landmarkNames[18] = "인문학관(인문대)";
        landmarkNames[19] = "학생회관";
        landmarkNames[20] = "제2학생회관";
        landmarkNames[21] = "공학관A";
        landmarkNames[22] = "공학관B";
        landmarkNames[23] = "공학관C";
        landmarkNames[24] = "신공학관";
        landmarkNames[25] = "과학관(이과대)";
        landmarkNames[26] = "창의관";
        landmarkNames[27] = "공예관";
        landmarkNames[28] = "국제학사";
        landmarkNames[29] = "기숙사";
        landmarkNames[30] = "일감호";
        landmarkNames[31] = "홍애교";
        landmarkNames[32] = "황소동상";
        landmarkNames[33] = "청심대";
        landmarkNames[34] = "상허박사 동상";
        landmarkNames[35] = "노천극장 중앙";
        landmarkNames[36] = "운동장";
        landmarkNames[37] = "실내체육관";
        landmarkNames[38] = "건국문(후문)";
        landmarkNames[39] = "중문";
        landmarkNames[40] = "일감문(동물병원)";
        landmarkNames[41] = "상허문(도서관)";
        landmarkNames[42] = "구의역쪽문";
        landmarkNames[43] = "기숙사쪽문";
        return landmarkNames;
    }

}
