package com.playkuround.playkuroundserver.infra.email.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.exception.ErrorCode;

public class EmailSendFailException extends BusinessException {

    public EmailSendFailException() {
        super(ErrorCode.EMAIL_SEND_FAIL);
    }

}
