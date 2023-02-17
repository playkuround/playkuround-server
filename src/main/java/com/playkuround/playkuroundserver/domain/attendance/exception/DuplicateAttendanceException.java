package com.playkuround.playkuroundserver.domain.attendance.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class DuplicateAttendanceException extends BusinessException {

    public DuplicateAttendanceException() {
        super(ErrorCode.DUPLICATE_ATTENDANCE);
    }

}
