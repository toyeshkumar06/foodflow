package com.foodflow.controller;

import com.foodflow.dto.RestaurantDtos.*;
import com.foodflow.entity.User;
import com.foodflow.service.RestaurantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping("/api/restaurant-owner/restaurants")
    public RestaurantResponse createRestaurant(@Valid @RequestBody CreateRestaurantRequest request,
                                                @AuthenticationPrincipal User owner) {
        return restaurantService.createRestaurant(request, owner);
    }

    @PatchMapping("/api/restaurant-owner/restaurants/{id}/status")
    public RestaurantResponse updateStatus(@PathVariable Long id,
                                            @Valid @RequestBody UpdateStatusRequest request,
                                            @AuthenticationPrincipal User owner) {
        return restaurantService.updateStatus(id, request.getStatus(), owner);
    }

    @GetMapping("/api/restaurant-owner/restaurants/mine")
    public List<RestaurantResponse> getMyRestaurants(@AuthenticationPrincipal User owner) {
        return restaurantService.getMyRestaurants(owner);
    }

    @GetMapping("/api/restaurants")
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/api/restaurants/{id}")
    public RestaurantResponse getById(@PathVariable Long id) {
        return restaurantService.getById(id);
    }
}