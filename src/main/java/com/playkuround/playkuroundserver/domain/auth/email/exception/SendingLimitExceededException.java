package com.playkuround.playkuroundserver.domain.auth.email.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class SendingLimitExceededException extends BusinessException {

    public SendingLimitExceededException() {
        super(ErrorCode.SENDING_LIMIT_EXCEEDED);
    }

}
