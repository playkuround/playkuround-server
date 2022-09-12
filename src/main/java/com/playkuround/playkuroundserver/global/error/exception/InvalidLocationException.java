package com.playkuround.playkuroundserver.global.error.exception;

public class InvalidLocationException extends BusinessException {

    public InvalidLocationException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

}
