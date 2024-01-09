package com.playkuround.playkuroundserver.global.util;

import java.time.LocalDate;

public class DateUtils {

    private DateUtils() {
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
