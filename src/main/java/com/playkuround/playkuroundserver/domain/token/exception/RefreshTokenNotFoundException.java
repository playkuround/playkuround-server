package com.playkuround.playkuroundserver.domain.token.exception;

import com.playkuround.playkuroundserver.global.error.exception.EntityNotFoundException;

public class RefreshTokenNotFoundException extends EntityNotFoundException {

    public RefreshTokenNotFoundException(String targetEmail) {
        super(targetEmail + " 의 RefreshToken 을 찾을 수 없습니다.");
    }

}
