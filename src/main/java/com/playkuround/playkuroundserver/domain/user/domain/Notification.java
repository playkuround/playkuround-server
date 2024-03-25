package com.playkuround.playkuroundserver.domain.user.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode
public class Notification {

    private final String name;
    private final String description;

    public Notification(NotificationEnum notificationEnum, String description) {
        Objects.requireNonNull(notificationEnum, "notificationEnum must be provided");

        this.name = notificationEnum.getName();
        this.description = (description == null ? "" : description);
    }
}
