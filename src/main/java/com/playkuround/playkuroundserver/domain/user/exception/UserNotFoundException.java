package com.playkuround.playkuroundserver.domain.user.exception;

import com.playkuround.playkuroundserver.global.error.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String targetEmail) {
        super(targetEmail + " 의 유저 엔티티 조회에 실패하였습니다.");
    }

    public UserNotFoundException() {
        super("유저 엔티티 조회에 실패하였습니다.");
    }
}
