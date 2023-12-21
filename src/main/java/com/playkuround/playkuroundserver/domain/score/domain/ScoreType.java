package com.playkuround.playkuroundserver.domain.score.domain;

import com.playkuround.playkuroundserver.domain.score.exception.ScoreTypeNotMatchException;

public enum ScoreType {
    INIT(0),
    ATTENDANCE(1),
    ADVENTURE(5),
    EXTRA_ADVENTURE(1);

    private final int point;

    ScoreType(int point) {
        this.point = point;
    }

    public int getPoint() {
        return point;
    }

    public static ScoreType fromString(String source) {
        try {
            return ScoreType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ScoreTypeNotMatchException();
        }
    }
}
