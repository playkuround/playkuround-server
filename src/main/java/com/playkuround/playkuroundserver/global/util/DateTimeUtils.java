package com.playkuround.playkuroundserver.global.util;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DateTimeUtils {

    public static LocalDateTime getMonthStartDateTime(LocalDate localDate) {
        return localDate.withDayOfMonth(1).atStartOfDay();
    }

    public static boolean isFoundationDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 5 && localDate.getDayOfMonth() == 15;
    }

    public static boolean isArborDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 4 && localDate.getDayOfMonth() == 5;
    }

    public static boolean isChildrenDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 5 && localDate.getDayOfMonth() == 5;
    }

    public static boolean isWhiteDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 3 && localDate.getDayOfMonth() == 14;
    }

    public static boolean isDuckDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 5 && localDate.getDayOfMonth() == 2;
    }

    public static boolean isChuseokDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 9 && localDate.getDayOfMonth() == 17;
    }

    public static boolean isKoreanDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 10 && localDate.getDayOfMonth() == 9;
    }

    public static boolean isDokdoDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 10 && localDate.getDayOfMonth() == 25;
    }

    public static boolean isKimchiDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 11 && localDate.getDayOfMonth() == 22;
    }

    public static boolean isChristmasDay(LocalDate localDate) {
        return localDate.getMonth().getValue() == 12 && localDate.getDayOfMonth() == 25;
    }

}
