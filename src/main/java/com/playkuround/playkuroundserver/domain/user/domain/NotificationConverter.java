package com.playkuround.playkuroundserver.domain.user.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.*;
import java.util.stream.Collectors;

@Converter
public class NotificationConverter implements AttributeConverter<Set<Notification>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Notification> notifications) {
        if (notifications == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Notification notification : notifications) {
            if (!sb.isEmpty()) {
                sb.append("@");
            }
            sb.append(notification.getName())
                    .append("#")
                    .append(notification.getDescription());
        }
        return sb.toString();
    }

    @Override
    public Set<Notification> convertToEntityAttribute(String notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(notifications.split("@"))
                .map(notification -> notification.split("#"))
                .filter(nameAndDescription -> nameAndDescription.length == 2)
                .map(nameAndDescription -> {
                    Optional<NotificationEnum> notificationEnum = NotificationEnum.fromString(nameAndDescription[0]);
                    return notificationEnum
                            .map(anEnum -> new Notification(anEnum, nameAndDescription[1]))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
