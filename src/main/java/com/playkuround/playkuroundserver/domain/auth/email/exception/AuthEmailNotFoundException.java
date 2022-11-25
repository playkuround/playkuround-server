package com.playkuround.playkuroundserver.domain.auth.email.exception;

import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;

public class AuthEmailNotFoundException extends EntityNotFoundException {

    public AuthEmailNotFoundException(String target) {
        super(target + " 의 AuthEmail 엔티티 조회에 실패하였습니다.");
    }

}
