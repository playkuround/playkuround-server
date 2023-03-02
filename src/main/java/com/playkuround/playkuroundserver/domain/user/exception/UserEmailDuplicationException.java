package com.playkuround.playkuroundserver.domain.user.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class UserEmailDuplicationException extends BusinessException {

    public UserEmailDuplicationException() {
        super(ErrorCode.EMAIL_DUPLICATION);
    }

}
