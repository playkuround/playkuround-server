package com.playkuround.playkuroundserver.domain.auth.email.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.exception.ErrorCode;

public class NotMatchAuthCodeException extends BusinessException {

    public NotMatchAuthCodeException() {
        super(ErrorCode.NOT_MATCH_AUTH_CODE);
    }

}
