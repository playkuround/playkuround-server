package com.playkuround.playkuroundserver.domain.badge.exception;

import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;

public class BadgeNotFoundException extends EntityNotFoundException {

    public BadgeNotFoundException(String targetEmail) {
        super(targetEmail + " 의 뱃지 조회에 실패하였습니다.");
    }

}
