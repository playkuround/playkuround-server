package com.playkuround.playkuroundserver.domain.auth.email.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.NotFoundException;

public class AuthEmailNotFoundException extends NotFoundException {

    public AuthEmailNotFoundException() {
        super(ErrorCode.EMAIL_NOT_FOUND);
    }

}
