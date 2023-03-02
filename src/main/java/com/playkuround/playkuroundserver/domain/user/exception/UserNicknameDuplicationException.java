package com.playkuround.playkuroundserver.domain.user.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class UserNicknameDuplicationException extends BusinessException {

    public UserNicknameDuplicationException() {
        super(ErrorCode.NICKNAME_DUPLICATION);
    }

}
