package com.playkuround.playkuroundserver.domain.quiz.exception;

import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;

public class QuizNotFoundException extends EntityNotFoundException {

    public QuizNotFoundException(Long landmarkId) {
        super(landmarkId + "의 랜드마크에 해당하는 퀴즈 조회에 실패하였습니다.");
    }

}
