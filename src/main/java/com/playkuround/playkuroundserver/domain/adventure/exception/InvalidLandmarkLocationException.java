package com.playkuround.playkuroundserver.domain.adventure.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.InvalidLocationException;

public class InvalidLandmarkLocationException extends InvalidLocationException {

    public InvalidLandmarkLocationException() {
        super(ErrorCode.INVALID_LOCATION_LANDMARK);
    }

}
