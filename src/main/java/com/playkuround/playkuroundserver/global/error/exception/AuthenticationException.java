package com.playkuround.playkuroundserver.global.error.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

}
