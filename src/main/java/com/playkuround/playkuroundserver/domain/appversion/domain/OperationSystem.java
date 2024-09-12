package com.playkuround.playkuroundserver.domain.appversion.domain;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum OperationSystem {
    ANDROID, IOS;

    private static final Map<String, OperationSystem> stringToEnum =
            Stream.of(values())
                    .collect(toMap(Object::toString, e -> e));

    public static Optional<OperationSystem> fromString(String os) {
        OperationSystem appVersion = stringToEnum.get(os.toUpperCase());
        return Optional.ofNullable(appVersion);
    }
}
