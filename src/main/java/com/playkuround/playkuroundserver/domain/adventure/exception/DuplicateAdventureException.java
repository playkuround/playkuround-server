package com.playkuround.playkuroundserver.domain.adventure.exception;

import com.playkuround.playkuroundserver.global.error.exception.BusinessException;
import com.playkuround.playkuroundserver.global.error.ErrorCode;

public class DuplicateAdventureException extends BusinessException {

    public DuplicateAdventureException() {
        super(ErrorCode.DUPLICATE_ADVENTURE);
    }

}
