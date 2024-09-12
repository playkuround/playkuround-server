package com.playkuround.playkuroundserver.domain.user.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class NotificationConverter implements AttributeConverter<Set<Notification>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return null;
        }

        return notifications.stream()
                .map(notification -> notification.getName() + "#" + notification.getDescription())
                .collect(Collectors.joining("@"));
    }

    @Override
    public Set<Notification> convertToEntityAttribute(String notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return new HashSet<>();
        }

        return Arrays.stream(notifications.split("@"))
                .map(notification -> notification.split("#"))
                .filter(nameAndDescription -> nameAndDescription.length == 2)
                .map(nameAndDescription -> NotificationEnum.fromString(nameAndDescription[0])
                        .map(notificationEnum -> new Notification(notificationEnum, nameAndDescription[1]))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
