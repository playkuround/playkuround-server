package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.score.api.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScoreAndBadgeType;
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
import static org.assertj.core.api.Assertions.tuple;
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
    void getRankTop100ByLandmark_1() {
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
    void getRankTop100ByLandmark_2() {
        // given
        List<NicknameAndScoreAndBadgeType> nicknameAndScores = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new NicknameAndScoreAndBadgeType("nickname" + (51 - i), 51 - i, null))
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
    void getRankTop100ByLandmark_3() {
        // given
        List<NicknameAndScoreAndBadgeType> nicknameAndScores = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new NicknameAndScoreAndBadgeType("nickname" + (51 - i), 51 - i, null))
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
    void getRankTop100ByLandmark_4() {
        // given
        List<NicknameAndScoreAndBadgeType> nicknameAndScores = IntStream.rangeClosed(1, 101)
                .mapToObj(i -> new NicknameAndScoreAndBadgeType("nickname" + (102 - i), 102 - i, null))
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

    @Test
    @DisplayName("랜드마크 랭킹 조회 결과에는 사용자 프로필 배지 데이터가 포함되어 있다.")
    void getRankTop100ByLandmark_5() {
        // given
        LocalDate now = LocalDate.of(2024, 7, 1);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(now);

        List<NicknameAndScoreAndBadgeType> rankData = List.of(
                new NicknameAndScoreAndBadgeType("user1", 10, BadgeType.ATTENDANCE_1),
                new NicknameAndScoreAndBadgeType("user2", 5, BadgeType.MONTHLY_RANKING_1)
        );

        Long landmarkId = 1L;
        when(adventureRepository.findRankTop100DescByLandmarkId(landmarkId, now.atStartOfDay()))
                .thenReturn(rankData);

        User user = TestUtil.createUser();
        user.updateProfileBadge(BadgeType.ATTENDANCE_CHRISTMAS_DAY);
        when(adventureRepository.findMyRankByLandmarkId(user, landmarkId, now.atStartOfDay()))
                .thenReturn(Optional.of(new RankAndScore(2, 7)));


        // when
        ScoreRankingResponse result = landmarkRankService.getRankTop100ByLandmark(user, landmarkId);

        // then
        assertThat(result.getRank()).hasSize(2)
                .extracting("nickname", "profileBadge", "score")
                .containsExactly(
                        tuple("user1", BadgeType.ATTENDANCE_1.name(), 10),
                        tuple("user2", BadgeType.MONTHLY_RANKING_1.name(), 5)
                );
        assertThat(result.getMyRank().getScore()).isEqualTo(7);
        assertThat(result.getMyRank().getRanking()).isEqualTo(2);
        assertThat(result.getMyRank().getProfileBadge()).isEqualTo(BadgeType.ATTENDANCE_CHRISTMAS_DAY.name());
    }
}