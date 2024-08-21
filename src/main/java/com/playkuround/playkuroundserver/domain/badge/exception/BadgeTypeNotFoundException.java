package com.playkuround.playkuroundserver.domain.badge.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.NotFoundException;

public class BadgeTypeNotFoundException extends NotFoundException {

    public BadgeTypeNotFoundException() {
        super(ErrorCode.INVALID_BADGE_TYPE);
    }
}

