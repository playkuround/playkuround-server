package com.playkuround.playkuroundserver.domain.badge.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.InvalidValueException;


public class BadgeTypeNotFoundException extends InvalidValueException {

    public BadgeTypeNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

