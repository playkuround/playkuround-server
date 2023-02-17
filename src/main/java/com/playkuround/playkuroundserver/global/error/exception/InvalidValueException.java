package com.playkuround.playkuroundserver.global.error.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class InvalidValueException extends BusinessException {

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

}
