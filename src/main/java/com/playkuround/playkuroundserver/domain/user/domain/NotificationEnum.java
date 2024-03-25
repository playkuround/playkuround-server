package com.playkuround.playkuroundserver.domain.user.domain;

import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Getter
public enum NotificationEnum {

    UPDATE("update", "application is must update"),
    SYSTEM_CHECK("system_check", "system is not available"),
    ALARM("alarm", "user message"),
    NEW_BADGE("new_badge", "user get new badge");

    private static final Map<String, NotificationEnum> stringToEnum =
            Stream.of(values())
                    .collect(toMap(NotificationEnum::getName, e -> e));
    private final String name;
    private final String defaultMessage;

    NotificationEnum(String name, String defaultMessage) {
        this.name = name;
        this.defaultMessage = defaultMessage;
    }

    public static Optional<NotificationEnum> fromString(String source) {
        return Optional.ofNullable(stringToEnum.get(source));
    }

}
