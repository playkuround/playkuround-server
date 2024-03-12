package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.LandmarkHighestScoreUser;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LandmarkScoreServiceTest {

    @InjectMocks
    private LandmarkScoreService landmarkScoreService;

    @Mock
    private LandmarkRepository landmarkRepository;

    @Test
    @DisplayName("해당 랜드마크의 가장 높은 점수를 가진 유저 반환")
    void findHighestScoreUserByLandmark() {
        // given
        User user = TestUtil.createUser();
        Landmark mockLandmark = mock(Landmark.class);
        when(mockLandmark.getHighestScore()).thenReturn(1234L);
        when(mockLandmark.getFirstUser()).thenReturn(user);
        when(landmarkRepository.findById(1L)).thenReturn(Optional.of(mockLandmark));

        // when
        LandmarkHighestScoreUser result = landmarkScoreService.findHighestScoreUserByLandmark(1L);

        // then
        assertThat(result.isHasResult()).isTrue();
        assertThat(result.getScore()).isEqualTo(1234L);
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("해당 랜드마크를 탐험한 유저가 없다면 빈 응답을 반환")
    void findHighestScoreUserByLandmarkEmpty() {
        // given
        Landmark mockLandmark = mock(Landmark.class);
        when(mockLandmark.getFirstUser()).thenReturn(null);
        when(landmarkRepository.findById(1L)).thenReturn(Optional.of(mockLandmark));

        // when
        LandmarkHighestScoreUser result = landmarkScoreService.findHighestScoreUserByLandmark(1L);

        // then
        assertThat(result.isHasResult()).isFalse();
    }

}