package com.playkuround.playkuroundserver.domain.score.exception;

import com.playkuround.playkuroundserver.global.error.exception.NotFoundException;

public class ScoreNotFoundException extends NotFoundException {

    public ScoreNotFoundException(String userEmail) {
        super(userEmail + " 의 Score 엔티티 조회에 실패하였습니다.");
    }

}
