package com.foodflow.util;

public class GeoUtils {

    private static final int EARTH_RADIUS_KM = 6371;

    // Haversine formula: standard way to calculate straight-line distance between two
    // lat/lng points on Earth. Real apps use road-distance from a maps API instead,
    // but this is the correct MVP approach and a common interview talking point
    // ("why not real road distance? cost/complexity tradeoff for an MVP").
    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}