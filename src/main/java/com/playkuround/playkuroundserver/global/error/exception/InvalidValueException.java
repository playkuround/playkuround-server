package com.playkuround.playkuroundserver.global.error.exception;

public class InvalidValueException extends BusinessException {

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

}
