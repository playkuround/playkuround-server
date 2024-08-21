package com.playkuround.playkuroundserver.domain.badge.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.InvalidValueException;

public class BadgeNotHaveException extends InvalidValueException {

    public BadgeNotHaveException() {
        super(ErrorCode.NOT_HAVE_BADGE);
    }
}

