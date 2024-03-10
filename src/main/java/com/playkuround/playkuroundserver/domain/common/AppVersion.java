package com.playkuround.playkuroundserver.domain.common;

public abstract class AppVersion {

    private AppVersion() {
    }

    private static String CURRENT_VERSION = "2.0.0";

    public static boolean isCurrentVersion(String version) {
        return CURRENT_VERSION.equals(version);
    }

    public static void changeAppVersion(String version) {
        CURRENT_VERSION = version;
    }

}
