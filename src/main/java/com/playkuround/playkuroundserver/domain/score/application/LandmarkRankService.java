package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScore;
import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
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

    @Transactional(readOnly = true)
    public ScoreRankingResponse getRankTop100ByLandmark(User user, Long landmarkId) {
        ScoreRankingResponse response = ScoreRankingResponse.createEmptyResponse();

        LocalDateTime monthStartDateTime = DateTimeUtils.getMonthStartDateTime();
        List<NicknameAndScore> nicknameAndScores = adventureRepository.findRankTop100DescByLandmarkId(landmarkId, monthStartDateTime);
        nicknameAndScores.forEach(nicknameAndScore ->
                response.addRank(nicknameAndScore.nickname(), nicknameAndScore.score()));

        RankAndScore rankAndScore = adventureRepository.findMyRankByLandmarkId(user, landmarkId)
                .orElseGet(() -> new RankAndScore(0, 0));
        response.setMyRank(rankAndScore.ranking(), rankAndScore.score());
        return response;
    }
}
