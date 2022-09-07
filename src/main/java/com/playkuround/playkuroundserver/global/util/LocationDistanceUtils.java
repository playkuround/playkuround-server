package com.playkuround.playkuroundserver.global.util;

public class LocationDistanceUtils {

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

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
