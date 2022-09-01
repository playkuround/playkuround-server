package com.playkuround.playkuroundserver.domain.score.exception;

import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;

public class ScoreNotFoundException extends EntityNotFoundException {

    public ScoreNotFoundException(String userEmail) {
        super(userEmail + " 의 Score 엔티티 조회에 실패하였습니다.");
    }

}
