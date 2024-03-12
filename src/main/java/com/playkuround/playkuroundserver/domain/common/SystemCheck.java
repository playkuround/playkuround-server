package com.playkuround.playkuroundserver.domain.common;

public abstract class SystemCheck {

    private static boolean systemAvailable = true;

    private SystemCheck() {
    }

    public static boolean isSystemAvailable() {
        return systemAvailable;
    }

    public static void changeSystemAvailable(boolean available) {
        systemAvailable = available;
    }

}
