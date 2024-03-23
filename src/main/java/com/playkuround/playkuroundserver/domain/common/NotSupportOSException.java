package com.playkuround.playkuroundserver.domain.common;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.BusinessException;

public class NotSupportOSException extends BusinessException {

    public NotSupportOSException() {
        super(ErrorCode.NOT_SUPPORT_OS);
    }

}
