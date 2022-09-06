package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dao.ScoreRepository;
import com.playkuround.playkuroundserver.domain.score.dto.ScoreRegisterDto;
import com.playkuround.playkuroundserver.domain.score.dto.ScoreRankingDto;
import com.playkuround.playkuroundserver.domain.score.exception.ScoreNotFoundException;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
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
    private final UserFindDao userFindDao;

    @Transactional
    public void saveScore(String userEmail, ScoreRegisterDto saveScore) {
        // TODO 검증의 범위는 어디까지? 예를 들면, 진짜로 유저가 탐험을 했는지 안했는지 확인까지 해야하는가?
        User user = userFindDao.findByEmail(userEmail);
        scoreRepository.save(saveScore.toEntity(user));
    }

    public List<ScoreRankingDto> getTop100() {
        return scoreRepository.findTop100();
    }

    public ScoreRankingDto getRanking(String userEmail) {
        User user = userFindDao.findByEmail(userEmail);
        return scoreRepository.findRankingByUser(user.getId())
                .orElseThrow(() -> new ScoreNotFoundException(userEmail));
    }

}
