package com.playkuround.playkuroundserver.domain.landmark.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.landmark.dao.LandmarkRepository;
import com.playkuround.playkuroundserver.domain.landmark.domain.Landmark;
import com.playkuround.playkuroundserver.domain.landmark.dto.LandmarkHighestScoreUser;
import com.playkuround.playkuroundserver.domain.landmark.exception.LandmarkNotFoundException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        Optional<LandmarkHighestScoreUser> result = landmarkScoreService.findHighestScoreUserByLandmark(1L);

        // then
        assertThat(result).isPresent()
                .hasValueSatisfying(firstUserData -> {
                    assertThat(firstUserData.score()).isEqualTo(1234L);
                    assertThat(firstUserData.nickname()).isEqualTo(user.getNickname());
                });
    }

    @Test
    @DisplayName("해당 랜드마크를 탐험한 유저가 없다면 빈 응답을 반환")
    void findHighestScoreUserByLandmarkEmpty() {
        // given
        Landmark mockLandmark = mock(Landmark.class);
        when(mockLandmark.getFirstUser()).thenReturn(null);
        when(landmarkRepository.findById(1L)).thenReturn(Optional.of(mockLandmark));

        // when
        Optional<LandmarkHighestScoreUser> result = landmarkScoreService.findHighestScoreUserByLandmark(1L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 랜드마크 ID로 조회 시 예외 발생")
    void findHighestScoreUserByNonExistentLandmark() {
        // given
        when(landmarkRepository.findById(999L)).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(
                () -> landmarkScoreService.findHighestScoreUserByLandmark(999L)
        ).isInstanceOf(LandmarkNotFoundException.class);
    }

}