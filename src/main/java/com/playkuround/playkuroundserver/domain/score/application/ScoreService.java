package com.playkuround.playkuroundserver.domain.score.application;

import com.playkuround.playkuroundserver.domain.score.dao.ScoreRepository;
import com.playkuround.playkuroundserver.domain.score.dto.SaveScore;
import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final UserFindDao userFindDao;

    public void saveScore(String userEmail, SaveScore saveScore) {
        // TODO 검증의 범위는 어디까지? 예를 들면, 진짜로 유저가 탐험을 했는지 안했는지 확인까지 해야하는가?
        User user = userFindDao.findByEmail(userEmail);

        scoreRepository.save(saveScore.toEntity(user));
    }

}
