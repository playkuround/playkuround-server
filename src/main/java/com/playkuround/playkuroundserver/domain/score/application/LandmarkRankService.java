package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.common.DateTimeService;
import com.playkuround.playkuroundserver.domain.score.api.response.TotalScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScoreAndBadgeType;
import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import com.playkuround.playkuroundserver.global.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LandmarkRankService {

    private final AdventureRepository adventureRepository;
    private final DateTimeService dateTimeService;

    @Transactional(readOnly = true)
    public TotalScoreRankingResponse getRankTop100ByLandmark(User user, Long landmarkId) {
        TotalScoreRankingResponse response = TotalScoreRankingResponse.createEmptyResponse();
        LocalDateTime monthStartDateTime = DateTimeUtils.getMonthStartDateTime(dateTimeService.getLocalDateNow());

        List<NicknameAndScoreAndBadgeType> nicknameAndScores = adventureRepository.findRankTop100DescByLandmarkId(landmarkId, monthStartDateTime);
        nicknameAndScores.forEach(nicknameAndScore ->
                response.addRank(nicknameAndScore.nickname(), nicknameAndScore.score(), nicknameAndScore.badgeType()));

        RankAndScore rankAndScore = adventureRepository.findMyRankByLandmarkId(user, landmarkId, monthStartDateTime)
                .orElseGet(() -> new RankAndScore(0, 0));
        response.setMyRank(rankAndScore.ranking(), rankAndScore.score(), user.getRepresentBadge());
        return response;
    }
}
