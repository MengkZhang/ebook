package com.tzpt.cloudlibrary.utils;

/**
 * Created by tonyjia on 2018/11/23.
 */

public class MapUtils {

    private static final double EARTH_RADIUS = 6378137.0;

    public static double getDistance(double latA, double lngA, double latB, double lngB) {
        double radLat1 = (latA * Math.PI / 180.0);
        double radLat2 = (latB * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lngA - lngB) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
