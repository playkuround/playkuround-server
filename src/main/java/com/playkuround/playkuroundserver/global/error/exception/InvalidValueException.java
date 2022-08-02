package com.playkuround.playkuroundserver.global.error.exception;

public class InvalidValueException extends BusinessException {

    public InvalidValueException(String value) {
        super(value, ErrorCode.INVALID_VALUE);
    }

    public InvalidValueException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }

}
