package com.playkuround.playkuroundserver.domain.auth.token.exception;

import com.playkuround.playkuroundserver.global.error.exception.AuthenticationException;
import com.playkuround.playkuroundserver.global.error.exception.ErrorCode;

public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }

}
