package com.foodflow.controller;

import com.foodflow.dto.RatingDtos.RateRequest;
import com.foodflow.dto.RatingDtos.RatingResponse;
import com.foodflow.entity.User;
import com.foodflow.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/{orderId}/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/restaurant")
    public RatingResponse rateRestaurant(@PathVariable Long orderId, @Valid @RequestBody RateRequest request,
                                          @AuthenticationPrincipal User customer) {
        return ratingService.rateRestaurant(orderId, request, customer);
    }

    @PostMapping("/food/{foodItemId}")
    public RatingResponse rateFoodItem(@PathVariable Long orderId, @PathVariable Long foodItemId,
                                        @Valid @RequestBody RateRequest request,
                                        @AuthenticationPrincipal User customer) {
        return ratingService.rateFoodItem(orderId, foodItemId, request, customer);
    }

    @PostMapping("/delivery-agent")
    public RatingResponse rateDeliveryAgent(@PathVariable Long orderId, @Valid @RequestBody RateRequest request,
                                             @AuthenticationPrincipal User customer) {
        return ratingService.rateDeliveryAgent(orderId, request, customer);
    }
}