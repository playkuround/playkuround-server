package com.playkuround.playkuroundserver.global.error.exception;


import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class IllegalException extends BusinessException {
    public IllegalException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public IllegalException(ErrorCode e) {
        super(e);
    }
}
