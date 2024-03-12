package com.playkuround.playkuroundserver.domain.user.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.BusinessException;

public class UserNicknameUnavailableException extends BusinessException {

    public UserNicknameUnavailableException() {
        super(ErrorCode.NICKNAME_UNAVAILABLE);
    }

}
