package com.playkuround.playkuroundserver.domain.score.domain;

import com.playkuround.playkuroundserver.domain.score.exception.ScoreTypeNotMatchException;

public enum ScoreType {

    ATTENDANCE,
    QUIZ, TIME, MOON, CARD, CATCH, HONG_BRIDGE, ALL_CLEAR, MICROBE;

    public static ScoreType fromString(String source) {
        try {
            return ScoreType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ScoreTypeNotMatchException();
        }
    }
}
