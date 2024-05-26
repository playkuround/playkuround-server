package com.playkuround.playkuroundserver.domain.appversion.domain;

import com.playkuround.playkuroundserver.domain.common.NotSupportOSException;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum OperationSystem {
    ANDROID, IOS;

    private static final Map<String, OperationSystem> stringToEnum =
            Stream.of(values())
                    .collect(toMap(Object::toString, e -> e));

    public static OperationSystem fromString(String os) {
        OperationSystem appVersion = stringToEnum.get(os.toUpperCase());
        if (appVersion == null) {
            throw new NotSupportOSException();
        }
        return appVersion;
    }
}
