package com.playkuround.playkuroundserver.domain.score.exception;

import com.playkuround.playkuroundserver.global.error.exception.InvalidValueException;

public class ScoreTypeNotMatchException extends InvalidValueException {

    public ScoreTypeNotMatchException() {
        super("scoreType이 올바르지 않습니다.");
    }

}
