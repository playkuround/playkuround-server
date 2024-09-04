package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.landmark.dto.NearestLandmark;
import com.playkuround.playkuroundserver.global.util.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LandmarkFindNearServiceTest {

    @InjectMocks
    private LandmarkFindNearService landmarkFindNearService;

    @Mock
    private LandmarkRepository landmarkRepository;

    @Test
    @DisplayName("인식 반경 내에 여러 랜드마크가 있다면, 중심 좌표와 더 가까운 랜드마크가 반환된다.")
    void findNearestLandmark() {
        // given
        Landmark mockLandmark1 = mock(Landmark.class);
        when(mockLandmark1.getId()).thenReturn(1L);
        when(mockLandmark1.getName()).thenReturn(LandmarkType.중문);
        when(mockLandmark1.getLatitude()).thenReturn(37.539927);
        when(mockLandmark1.getLongitude()).thenReturn(127.073006);
        when(mockLandmark1.getRecognitionRadius()).thenReturn(20);

        Landmark mockLandmark2 = mock(Landmark.class);
        when(mockLandmark2.getLatitude()).thenReturn(37.53992);
        when(mockLandmark2.getLongitude()).thenReturn(127.07300);
        when(mockLandmark2.getRecognitionRadius()).thenReturn(100);

        List<Landmark> landmarks = List.of(mockLandmark1, mockLandmark2);
        when(landmarkRepository.findAll()).thenReturn(landmarks);

        Location location = new Location(mockLandmark1.getLatitude(), mockLandmark1.getLongitude());

        // when
        NearestLandmark nearestLandmark = landmarkFindNearService.findNearestLandmark(location);

        // then
        assertThat(nearestLandmark.getLandmarkId()).isEqualTo(mockLandmark1.getId());
        assertThat(nearestLandmark.getName()).isEqualTo(mockLandmark1.getName().name());
    }

    @Test
    @DisplayName("인식 반경 내에 랜드마크가 없다면 결과 데이터가 없다.")
    void findNearestLandmarkEmpty() {
        // given
        Landmark mockLandmark = mock(Landmark.class);
        when(mockLandmark.getLatitude()).thenReturn(37.539927);
        when(mockLandmark.getLongitude()).thenReturn(127.073006);
        when(mockLandmark.getRecognitionRadius()).thenReturn(5);

        List<Landmark> landmarks = List.of(mockLandmark);
        when(landmarkRepository.findAll()).thenReturn(landmarks);

        Location location = new Location(0, 0);

        // when
        NearestLandmark nearestLandmark = landmarkFindNearService.findNearestLandmark(location);

        // then
        assertThat(nearestLandmark.getLandmarkId()).isNull();
    }

}