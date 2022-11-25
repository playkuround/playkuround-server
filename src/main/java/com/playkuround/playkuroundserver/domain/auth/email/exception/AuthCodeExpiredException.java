package com.playkuround.playkuroundserver.domain.auth.email.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.exception.ErrorCode;

public class AuthCodeExpiredException extends BusinessException {

    public AuthCodeExpiredException() {
        super(ErrorCode.EXPIRED_AUTH_CODE);
    }

}
