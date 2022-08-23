package com.playkuround.playkuroundserver.domain.user.exception;

import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String targetEmail) {
        super(targetEmail + " 의 유저 엔티티 조회에 실패하였습니다.");
    }

}
