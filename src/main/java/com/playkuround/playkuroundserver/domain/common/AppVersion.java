package com.playkuround.playkuroundserver.domain.common;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum AppVersion {

    ANDROID("2.0.3"),
    IOS("2.0.0");

    private static final Map<String, AppVersion> stringToEnum =
            Stream.of(values())
                    .collect(toMap(Object::toString, e -> e));
    private String latest_updated_version;

    AppVersion(String latest_updated_version) {
        this.latest_updated_version = latest_updated_version;
    }

    public static boolean isLatestUpdatedVersion(String os, String version) {
        AppVersion appVersion = stringToEnum.get(os.toUpperCase());
        if (appVersion == null) {
            throw new NotSupportOSException();
        }
        return appVersion.latest_updated_version.equals(version);
    }

    public static void changeLatestUpdatedVersion(String os, String version) {
        AppVersion appVersion = stringToEnum.get(os.toUpperCase());
        if (appVersion == null) {
            throw new NotSupportOSException();
        }
        appVersion.latest_updated_version = version;
    }

}
