package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.score.api.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScore;
import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LandmarkRankServiceTest {

    @InjectMocks
    private LandmarkRankService landmarkRankService;

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private DateTimeService dateTimeService;

    @Test
    @DisplayName("랭킹 유저가 한명도 없을 때")
    void getRankTop100ByLandmark1() {
        // given
        when(adventureRepository.findRankTop100DescByLandmarkId(any(Long.class), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(adventureRepository.findMyRankByLandmarkId(any(User.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(dateTimeService.getLocalDateNow())
                .thenReturn(LocalDate.of(2024, 7, 1));

        // when
        User user = TestUtil.createUser();
        ScoreRankingResponse result = landmarkRankService.getRankTop100ByLandmark(user, 1L);

        // then
        assertThat(result.getRank()).isEmpty();
        assertThat(result.getMyRank().getRanking()).isZero();
        assertThat(result.getMyRank().getScore()).isZero();
    }

    @Test
    @DisplayName("전체 유저 100명 미만 + 내 랭킹은 없음")
    void getRankTop100ByLandmark2() {
        // given
        List<NicknameAndScore> nicknameAndScores = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new NicknameAndScore("nickname" + (51 - i), 51 - i))
                .toList();
        when(adventureRepository.findRankTop100DescByLandmarkId(any(Long.class), any(LocalDateTime.class)))
                .thenReturn(nicknameAndScores);
        when(adventureRepository.findMyRankByLandmarkId(any(User.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(dateTimeService.getLocalDateNow())
                .thenReturn(LocalDate.of(2024, 7, 1));

        // when
        User user = TestUtil.createUser();
        ScoreRankingResponse result = landmarkRankService.getRankTop100ByLandmark(user, 1L);

        // then
        List<ScoreRankingResponse.RankList> rankList = result.getRank();
        assertThat(rankList).hasSize(50);
        for (int i = 50; i > 0; i--) {
            assertThat(rankList.get(50 - i).getNickname()).isEqualTo("nickname" + i);
            assertThat(rankList.get(50 - i).getScore()).isEqualTo(i);
        }

        assertThat(result.getMyRank().getRanking()).isZero();
        assertThat(result.getMyRank().getScore()).isZero();
    }

    @Test
    @DisplayName("전체 유저 100명 미만 + 내 랭킹 존재")
    void getRankTop100ByLandmark3() {
        // given
        List<NicknameAndScore> nicknameAndScores = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new NicknameAndScore("nickname" + (51 - i), 51 - i))
                .toList();
        when(adventureRepository.findRankTop100DescByLandmarkId(any(Long.class), any(LocalDateTime.class)))
                .thenReturn(nicknameAndScores);
        when(adventureRepository.findMyRankByLandmarkId(any(User.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new RankAndScore(14, 37)));
        when(dateTimeService.getLocalDateNow())
                .thenReturn(LocalDate.of(2024, 7, 1));

        // when
        User user = TestUtil.createUser();
        ScoreRankingResponse result = landmarkRankService.getRankTop100ByLandmark(user, 1L);

        // then
        List<ScoreRankingResponse.RankList> rankList = result.getRank();
        assertThat(rankList).hasSize(50);
        for (int i = 50; i > 0; i--) {
            assertThat(rankList.get(50 - i).getNickname()).isEqualTo("nickname" + i);
            assertThat(rankList.get(50 - i).getScore()).isEqualTo(i);
        }
        assertThat(result.getMyRank().getScore()).isEqualTo(37);
        assertThat(result.getMyRank().getRanking()).isEqualTo(14);
    }

    @Test
    @DisplayName("전체 유저 100명 초과 + 내 랭킹 중간에 존재")
    void getRankTop100ByLandmark4() {
        // given
        List<NicknameAndScore> nicknameAndScores = IntStream.rangeClosed(1, 101)
                .mapToObj(i -> new NicknameAndScore("nickname" + (102 - i), 102 - i))
                .toList();
        when(adventureRepository.findRankTop100DescByLandmarkId(any(Long.class), any(LocalDateTime.class)))
                .thenReturn(nicknameAndScores);
        when(adventureRepository.findMyRankByLandmarkId(any(User.class), any(Long.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new RankAndScore(40, 62)));
        when(dateTimeService.getLocalDateNow())
                .thenReturn(LocalDate.of(2024, 7, 1));

        // when
        User user = TestUtil.createUser();
        ScoreRankingResponse result = landmarkRankService.getRankTop100ByLandmark(user, 1L);

        // then
        List<ScoreRankingResponse.RankList> rankList = result.getRank();
        assertThat(rankList).hasSize(101);
        for (int i = 101; i > 0; i--) {
            assertThat(rankList.get(101 - i).getNickname()).isEqualTo("nickname" + i);
            assertThat(rankList.get(101 - i).getScore()).isEqualTo(i);
        }
        assertThat(result.getMyRank().getScore()).isEqualTo(62);
        assertThat(result.getMyRank().getRanking()).isEqualTo(40);
    }
}