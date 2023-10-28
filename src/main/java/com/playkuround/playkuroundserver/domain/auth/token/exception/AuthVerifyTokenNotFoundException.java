package com.playkuround.playkuroundserver.domain.auth.token.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.NotFoundException;

public class AuthVerifyTokenNotFoundException extends NotFoundException {

    public AuthVerifyTokenNotFoundException() {
        super(ErrorCode.INVALID_TOKEN);
    }

}
