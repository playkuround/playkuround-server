package com.playkuround.playkuroundserver.domain.auth.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.exception.ErrorCode;

public class NotKUEmailException extends BusinessException {

    public NotKUEmailException() {
        super(ErrorCode.NOT_KU_EMAIL);
    }

}
