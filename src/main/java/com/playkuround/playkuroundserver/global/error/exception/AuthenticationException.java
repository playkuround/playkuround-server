package com.playkuround.playkuroundserver.global.error.exception;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

}
