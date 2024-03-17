package com.playkuround.playkuroundserver.domain.score.domain;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum ScoreType {

    ATTENDANCE,
    QUIZ, TIME, MOON, BOOK, CATCH, CUPID, ALL_CLEAR, SURVIVE;

    private static final Map<String, ScoreType> stringToEnum =
            Stream.of(values())
                    .collect(toMap(Object::toString, e -> e));

    public static Optional<ScoreType> fromString(String source) {
        return Optional.ofNullable(stringToEnum.get(source));
    }
}
