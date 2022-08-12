package com.playkuround.playkuroundserver.domain.adventure.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.exception.ErrorCode;

public class LocationValidateException extends BusinessException {

    public LocationValidateException() {
        super(ErrorCode.LOCATION_INVALID);
    }

}
