package com.playkuround.playkuroundserver.global.error.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }

    public NotFoundException(ErrorCode e) {
        super(e);
    }

}
