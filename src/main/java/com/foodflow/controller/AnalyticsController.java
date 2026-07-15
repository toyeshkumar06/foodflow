package com.foodflow.controller;

import com.foodflow.dto.AnalyticsDtos.AdminOverviewResponse;
import com.foodflow.dto.AnalyticsDtos.RestaurantAnalyticsResponse;
import com.foodflow.entity.User;
import com.foodflow.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/api/admin/analytics/overview")
    public AdminOverviewResponse getAdminOverview() {
        return analyticsService.getAdminOverview();
    }

    @GetMapping("/api/restaurant-owner/restaurants/{id}/analytics")
    public RestaurantAnalyticsResponse getRestaurantAnalytics(@PathVariable Long id,
                                                                @AuthenticationPrincipal User owner) {
        return analyticsService.getRestaurantAnalytics(id, owner);
    }
}