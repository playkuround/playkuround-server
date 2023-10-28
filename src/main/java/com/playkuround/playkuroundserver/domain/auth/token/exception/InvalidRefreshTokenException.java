package com.playkuround.playkuroundserver.domain.auth.token.exception;


import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.IllegalException;

public class InvalidRefreshTokenException extends IllegalException {
    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
