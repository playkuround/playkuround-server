package com.playkuround.playkuroundserver.domain.score.exception;

import com.playkuround.playkuroundserver.global.error.ErrorCode;
import com.playkuround.playkuroundserver.global.error.exception.InvalidValueException;

public class ScoreTypeNotMatchException extends InvalidValueException {

    public ScoreTypeNotMatchException() {
        super(ErrorCode.INVALID_SCORE_TYPE);
    }

}
