package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.adventure.dao.AdventureRepository;
import com.playkuround.playkuroundserver.domain.score.dto.NicknameAndScore;
import com.playkuround.playkuroundserver.domain.score.dto.RankAndScore;
import com.playkuround.playkuroundserver.domain.score.dto.response.ScoreRankingResponse;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LandmarkRankService {

    private final AdventureRepository adventureRepository;

    @Transactional(readOnly = true)
    public ScoreRankingResponse getRankTop100ByLandmark(User user, Long landmarkId) {
        List<NicknameAndScore> nicknameAndScores = adventureRepository.findRankTop100DescByLandmarkId(landmarkId);

        ScoreRankingResponse response = ScoreRankingResponse.createEmptyResponse();
        nicknameAndScores.forEach(nicknameAndScore -> response.addRank(nicknameAndScore.nickname(), nicknameAndScore.score()));

        Optional<RankAndScore> optionalMyScore = adventureRepository.findMyRankByLandmarkId(user, landmarkId);
        if (optionalMyScore.isPresent()) {
            RankAndScore myScore = optionalMyScore.get();
            response.setMyRank(myScore.ranking(), myScore.score());
        }
        return response;
    }
}
