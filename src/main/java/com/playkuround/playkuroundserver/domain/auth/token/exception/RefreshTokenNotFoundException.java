package com.playkuround.playkuroundserver.domain.auth.token.exception;

import com.playkuround.playkuroundserver.global.error.exception.NotFoundException;

public class RefreshTokenNotFoundException extends NotFoundException {

    public RefreshTokenNotFoundException(String target) {
        super(target + " 의 리프레시 토큰이 존재하지 않습니다.");
    }

}
