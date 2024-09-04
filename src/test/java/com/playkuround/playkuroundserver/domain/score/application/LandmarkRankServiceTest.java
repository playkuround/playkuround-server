package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.TestUtil;
import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.badge.domain.BadgeType;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.score.api.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScoreAndBadgeType;
import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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
    @DisplayName("랭킹 유저가 한명도 없으면 빈 응답을 반환한다.")
    void getRankTop100ByLandmark_1() {
        // given
        LocalDate localDate = LocalDate.of(2024, 7, 1);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(localDate);

        Long landmarkId = 1L;
        LocalDateTime startDateTime = DateTimeUtils.getMonthStartDateTime(localDate);
        when(adventureRepository.findRankTop100DescByLandmarkId(landmarkId, startDateTime))
                .thenReturn(new ArrayList<>());

        User user = TestUtil.createUser();
        when(adventureRepository.findMyRankByLandmarkId(user, landmarkId, startDateTime))
                .thenReturn(Optional.empty());

        // when
        ScoreRankingResponse result = landmarkRankService.getRankTop100ByLandmark(user, landmarkId);

        // then
        assertThat(result.getRank()).isEmpty();
        assertThat(result.getMyRank().getRanking()).isZero();
        assertThat(result.getMyRank().getScore()).isZero();
    }

    @Test
    @DisplayName("전체 유저 100명 미만 + 내 랭킹은 없음")
    void getRankTop100ByLandmark_2() {
        // given
        LocalDate localDate = LocalDate.of(2024, 7, 1);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(localDate);

        Long landmarkId = 1L;
        LocalDateTime startDateTime = DateTimeUtils.getMonthStartDateTime(localDate);
        List<NicknameAndScoreAndBadgeType> rankData = List.of(
                new NicknameAndScoreAndBadgeType("user1", 100, BadgeType.ATTENDANCE_1),
                new NicknameAndScoreAndBadgeType("user2", 50, BadgeType.ATTENDANCE_10),
                new NicknameAndScoreAndBadgeType("user3", 1, BadgeType.ATTENDANCE_100)
        );
        when(adventureRepository.findRankTop100DescByLandmarkId(landmarkId, startDateTime))
                .thenReturn(rankData);

        User user = TestUtil.createUser();
        when(adventureRepository.findMyRankByLandmarkId(user, landmarkId, startDateTime))
                .thenReturn(Optional.empty());

        // when
        ScoreRankingResponse result = landmarkRankService.getRankTop100ByLandmark(user, landmarkId);

        // then
        List<ScoreRankingResponse.RankList> rankList = result.getRank();
        assertThat(rankList).hasSize(3)
                .extracting("nickname", "score", "profileBadge")
                .containsExactly(
                        tuple("user1", 100, BadgeType.ATTENDANCE_1.name()),
                        tuple("user2", 50, BadgeType.ATTENDANCE_10.name()),
                        tuple("user3", 1, BadgeType.ATTENDANCE_100.name())
                );
        assertThat(result.getMyRank().getRanking()).isZero();
        assertThat(result.getMyRank().getScore()).isZero();
    }

    @Test
    @DisplayName("전체 유저 100명 미만 + 내 랭킹 존재")
    void getRankTop100ByLandmark_3() {
        // given
        LocalDate localDate = LocalDate.of(2024, 7, 1);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(localDate);

        Long landmarkId = 1L;
        LocalDateTime startDateTime = DateTimeUtils.getMonthStartDateTime(localDate);
        List<NicknameAndScoreAndBadgeType> rankData = List.of(
                new NicknameAndScoreAndBadgeType("user1", 100, BadgeType.ATTENDANCE_1),
                new NicknameAndScoreAndBadgeType("user2", 50, BadgeType.ATTENDANCE_10),
                new NicknameAndScoreAndBadgeType("user3", 1, BadgeType.ATTENDANCE_100)
        );
        when(adventureRepository.findRankTop100DescByLandmarkId(landmarkId, startDateTime))
                .thenReturn(rankData);

        User user = TestUtil.createUser();
        RankAndScore myRank = new RankAndScore(14, 37);
        when(adventureRepository.findMyRankByLandmarkId(user, landmarkId, startDateTime))
                .thenReturn(Optional.of(myRank));

        // when
        ScoreRankingResponse result = landmarkRankService.getRankTop100ByLandmark(user, landmarkId);

        // then
        List<ScoreRankingResponse.RankList> rankList = result.getRank();
        assertThat(rankList).hasSize(3)
                .extracting("nickname", "score", "profileBadge")
                .containsExactly(
                        tuple("user1", 100, BadgeType.ATTENDANCE_1.name()),
                        tuple("user2", 50, BadgeType.ATTENDANCE_10.name()),
                        tuple("user3", 1, BadgeType.ATTENDANCE_100.name())
                );
        assertThat(result.getMyRank().getScore()).isEqualTo(myRank.score());
        assertThat(result.getMyRank().getRanking()).isEqualTo(myRank.ranking());
    }

    @Test
    @DisplayName("전체 유저 100명 초과 + 내 랭킹 중간에 존재")
    void getRankTop100ByLandmark_4() {
        // given
        LocalDate localDate = LocalDate.of(2024, 7, 1);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(localDate);

        Long landmarkId = 1L;
        LocalDateTime startDateTime = DateTimeUtils.getMonthStartDateTime(localDate);
        List<NicknameAndScoreAndBadgeType> nicknameAndScores = IntStream.rangeClosed(1, 101)
                .mapToObj(i -> new NicknameAndScoreAndBadgeType("nickname" + (102 - i), 102 - i, null))
                .toList();
        when(adventureRepository.findRankTop100DescByLandmarkId(landmarkId, startDateTime))
                .thenReturn(nicknameAndScores);

        User user = TestUtil.createUser();
        RankAndScore myRank = new RankAndScore(40, 62);
        when(adventureRepository.findMyRankByLandmarkId(user, landmarkId, startDateTime))
                .thenReturn(Optional.of(myRank));

        // when
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
        LocalDate localDate = LocalDate.of(2024, 7, 1);
        when(dateTimeService.getLocalDateNow())
                .thenReturn(localDate);

        Long landmarkId = 1L;
        LocalDateTime startDateTime = DateTimeUtils.getMonthStartDateTime(localDate);
        List<NicknameAndScoreAndBadgeType> rankData = List.of(
                new NicknameAndScoreAndBadgeType("user1", 10, BadgeType.ATTENDANCE_1),
                new NicknameAndScoreAndBadgeType("user2", 5, BadgeType.MONTHLY_RANKING_1)
        );
        when(adventureRepository.findRankTop100DescByLandmarkId(landmarkId, startDateTime))
                .thenReturn(rankData);

        User user = TestUtil.createUser();
        user.updateProfileBadge(BadgeType.ATTENDANCE_CHRISTMAS_DAY);
        RankAndScore myRank = new RankAndScore(2, 7);
        when(adventureRepository.findMyRankByLandmarkId(user, landmarkId, startDateTime))
                .thenReturn(Optional.of(myRank));

        // when
        ScoreRankingResponse result = landmarkRankService.getRankTop100ByLandmark(user, landmarkId);

        // then
        assertThat(result.getRank()).hasSize(2)
                .extracting("nickname", "profileBadge", "score")
                .containsExactly(
                        tuple("user1", BadgeType.ATTENDANCE_1.name(), 10),
                        tuple("user2", BadgeType.MONTHLY_RANKING_1.name(), 5)
                );
        assertThat(result.getMyRank().getScore()).isEqualTo(myRank.score());
        assertThat(result.getMyRank().getRanking()).isEqualTo(myRank.ranking());
        assertThat(result.getMyRank().getProfileBadge()).isEqualTo(user.getProfileBadge().name());
    }
}