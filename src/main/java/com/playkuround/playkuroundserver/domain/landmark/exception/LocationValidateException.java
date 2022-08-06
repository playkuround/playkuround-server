package com.playkuround.playkuroundserver.domain.landmark.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.exception.ErrorCode;

public class LocationValidateException extends BusinessException {

    public LocationValidateException() {
        super(ErrorCode.LOCATION_VALIDATE);
    }

}
