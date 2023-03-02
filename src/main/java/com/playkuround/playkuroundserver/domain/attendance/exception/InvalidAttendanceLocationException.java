package com.playkuround.playkuroundserver.domain.attendance.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.InvalidLocationException;

public class InvalidAttendanceLocationException extends InvalidLocationException {

    public InvalidAttendanceLocationException() {
        super(ErrorCode.INVALID_LOCATION_KU);
    }

}
