package com.playkuround.playkuroundserver.domain.common;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeService {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
