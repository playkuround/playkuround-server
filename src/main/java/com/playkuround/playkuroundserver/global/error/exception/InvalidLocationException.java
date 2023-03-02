package com.playkuround.playkuroundserver.global.error.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class InvalidLocationException extends BusinessException {

    public InvalidLocationException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

}
