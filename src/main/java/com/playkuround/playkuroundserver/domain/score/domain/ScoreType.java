package com.playkuround.playkuroundserver.domain.score.domain;

public enum ScoreType {
    ATTENDANCE(1), ADVENTURE(5), EXTRA_ADVENTURE(1);

    private final int point;

    ScoreType(int point) {
        this.point = point;
    }

    public int getPoint() {
        return point;
    }
}
