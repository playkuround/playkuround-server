package com.playkuround.playkuroundserver.global.util;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DateTimeUtils {

    public static LocalDateTime getMonthStartDateTime() {
        LocalDate today = LocalDate.now();
        return today.withDayOfMonth(1).atStartOfDay();
    }

    public static boolean isTodayFoundationDay() {
        LocalDate today = LocalDate.now();
        return today.getMonth().getValue() == 5 && today.getDayOfMonth() == 15;
    }

    public static boolean isTodayArborDay() {
        LocalDate today = LocalDate.now();
        return today.getMonth().getValue() == 4 && today.getDayOfMonth() == 5;
    }

    public static boolean isTodayChildrenDay() {
        LocalDate today = LocalDate.now();
        return today.getMonth().getValue() == 5 && today.getDayOfMonth() == 5;
    }

    public static boolean isTodayWhiteDay() {
        LocalDate today = LocalDate.now();
        return today.getMonth().getValue() == 3 && today.getDayOfMonth() == 14;
    }

    public static boolean isTodayDuckDay() {
        LocalDate today = LocalDate.now();
        return today.getMonth().getValue() == 5 && today.getDayOfMonth() == 2;
    }

}
