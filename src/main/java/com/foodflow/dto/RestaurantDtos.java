package com.foodflow.dto;

import com.foodflow.entity.RestaurantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

public class RestaurantDtos {

    @Data
    public static class CreateRestaurantRequest {
        @NotBlank private String name;
        private String description;
        private String cuisineType;
        @NotBlank private String addressLine;
        @NotBlank private String city;
        private String pincode;
        private String openingTime; // send as "09:00"
        private String closingTime; // send as "23:00"
    }

    @Data
    public static class UpdateStatusRequest {
        @NotNull private RestaurantStatus status;
    }

    @Data
    @AllArgsConstructor
    public static class RestaurantResponse {
        private Long id;
        private String name;
        private String description;
        private String cuisineType;
        private String addressLine;
        private String city;
        private String pincode;
        private RestaurantStatus status;
        private LocalTime openingTime;
        private LocalTime closingTime;
        private Double averageRating;
        private Long ownerId;
    }
}