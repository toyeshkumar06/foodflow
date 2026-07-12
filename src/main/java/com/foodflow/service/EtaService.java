package com.foodflow.service;

import org.springframework.stereotype.Service;

@Service
public class EtaService {

    private static final double AVERAGE_SPEED_KMPH = 30.0;
    private static final double TRAFFIC_MULTIPLIER = 1.5; // flat assumption for MVP

    public int calculateEtaMinutes(int prepTimeMinutes, double distanceKm) {
        double travelTimeMinutes = (distanceKm / AVERAGE_SPEED_KMPH) * 60 * TRAFFIC_MULTIPLIER;
        return prepTimeMinutes + (int) Math.round(travelTimeMinutes);
    }
}