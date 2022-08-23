package com.playkuround.playkuroundserver.domain.score.dao;

import com.playkuround.playkuroundserver.domain.user.dao.UserFindDao;
import com.playkuround.playkuroundserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoreFindDao {

    private final ScoreRepository scoreRepository;
    private final UserFindDao userFindDao;

    public int findTotalScorePointByUserEmail(String email) {
        User user = userFindDao.findByEmail(email);

        return scoreRepository.findByUser(user).stream()
                .mapToInt(score -> score.getScoreType().getPoint())
                .sum();
    }
}
