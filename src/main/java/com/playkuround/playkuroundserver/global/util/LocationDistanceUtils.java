package com.playkuround.playkuroundserver.global.util;

public class LocationDistanceUtils {

    public static double distance(Location location1, Location location2) {
        double theta = location1.longitude() - location2.longitude();
        double dist = Math.sin(deg2rad(location1.latitude())) * Math.sin(deg2rad(location2.latitude()))
                + Math.cos(deg2rad(location1.latitude())) * Math.cos(deg2rad(location2.latitude())) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return dist * 1609.344; // 미터 변환
    }

    private static double deg2rad(double deg) {
        // converts decimal degrees to radians
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        // converts radians to decimal degrees
        return (rad * 180 / Math.PI);
    }

}
