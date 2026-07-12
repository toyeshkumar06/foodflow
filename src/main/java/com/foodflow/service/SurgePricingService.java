package com.foodflow.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SurgePricingService {

    // When active orders outnumber available online agents, delivery gets more expensive.
    public BigDecimal calculateSurgeMultiplier(long activeOrders, long onlineAgents) {
        if (onlineAgents == 0) {
            return BigDecimal.valueOf(2.0); // no agents online at all -> treat as max demand pressure
        }

        double ratio = (double) activeOrders / onlineAgents;

        if (ratio <= 1.0) {
            return BigDecimal.ONE; // agents can keep up, no surge
        }

        // every extra unit of ratio above 1.0 adds 0.5x, capped at 2.5x total
        double multiplier = 1.0 + Math.min((ratio - 1.0) * 0.5, 1.5);
        return BigDecimal.valueOf(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}