package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dao.ScoreRepository;
import com.playkuround.playkuroundserver.domain.score.domain.ScoreType;
import com.playkuround.playkuroundserver.domain.score.dto.ScoreRankingDto;
import com.playkuround.playkuroundserver.domain.score.exception.ScoreNotFoundException;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoreService {

    private final ScoreRepository scoreRepository;

    @Transactional
    public int saveScore(User user, ScoreType scoreType, Integer score) {
        return 0; // 보정 후 점수
    }

    public List<ScoreRankingDto> getTop100() {
        return scoreRepository.findTop100();
    }

    public ScoreRankingDto getRanking(User user) {
        return scoreRepository.findRankingByUser(user.getId())
                .orElseThrow(() -> new ScoreNotFoundException(user.getEmail()));
    }

}
