package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.domain.LandmarkType;
import com.playkuround.playkuroundserver.domain.landmark.dto.response.NearestLandmarkResponse;
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
    @DisplayName("가장 가까운 랜드마크 조회")
    void findNearestLandmark() {
        // given
        Landmark mockLandmark1 = mock(Landmark.class);
        when(mockLandmark1.getId()).thenReturn(1L);
        when(mockLandmark1.getLatitude()).thenReturn(37.539927);
        when(mockLandmark1.getName()).thenReturn(LandmarkType.중문);
        when(mockLandmark1.getLongitude()).thenReturn(127.073006);
        when(mockLandmark1.getRecognitionRadius()).thenReturn(20);

        Landmark mockLandmark2 = mock(Landmark.class);
        when(mockLandmark2.getLatitude()).thenReturn(37.0);
        when(mockLandmark2.getLongitude()).thenReturn(127.0);
        when(mockLandmark2.getRecognitionRadius()).thenReturn(20);

        List<Landmark> landmarks = List.of(mockLandmark1, mockLandmark2);
        when(landmarkRepository.findAll()).thenReturn(landmarks);

        // when
        Location location = new Location(37.539927, 127.073006);
        NearestLandmarkResponse response = landmarkFindNearService.findNearestLandmark(location);

        // then
        assertThat(response.getLandmarkId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo(LandmarkType.중문.name());
    }

    @Test
    @DisplayName("인식 반경에 랜드마크가 없다면 빈 응답을 반환")
    void findNearestLandmarkEmpty() {
        // given
        Landmark mockLandmark1 = mock(Landmark.class);
        when(mockLandmark1.getLatitude()).thenReturn(37.539927);
        when(mockLandmark1.getLongitude()).thenReturn(127.073006);
        when(mockLandmark1.getRecognitionRadius()).thenReturn(5);

        Landmark mockLandmark2 = mock(Landmark.class);
        when(mockLandmark2.getLatitude()).thenReturn(37.0);
        when(mockLandmark2.getLongitude()).thenReturn(127.0);
        when(mockLandmark2.getRecognitionRadius()).thenReturn(3);

        List<Landmark> landmarks = List.of(mockLandmark1, mockLandmark2);
        when(landmarkRepository.findAll()).thenReturn(landmarks);

        // when
        Location location = new Location(37.539, 127.0736);
        NearestLandmarkResponse response = landmarkFindNearService.findNearestLandmark(location);

        // then
        assertThat(response.getName()).isNull();
        assertThat(response.getDistance()).isNull();
        assertThat(response.getLandmarkId()).isNull();
    }

}