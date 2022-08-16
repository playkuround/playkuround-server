package com.playkuround.playkuroundserver.domain.auth.token.exception;

import com.playkuround.playkuroundserver.global.error.exception.InvalidValueException;

public class InvalidTokenException extends InvalidValueException {

    public InvalidTokenException(String token) {
        super(token + " 은(는) 유효하지 않은 토큰입니다.");
    }

}
