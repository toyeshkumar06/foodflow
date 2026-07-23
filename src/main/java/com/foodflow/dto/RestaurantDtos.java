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
        private String openingTime;
        private String closingTime;
        private Double latitude;
        private Double longitude;
        private String imageUrl;
    }

    @Data
    public static class UpdateStatusRequest {
        @NotNull private RestaurantStatus status;
    }

    @Data
    public static class UpdateLocationRequest {
        @NotNull private Double latitude;
        @NotNull private Double longitude;
    }

    @Data
    public static class UpdateImageRequest {
        @NotBlank private String imageUrl;
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
        private Double latitude;
        private Double longitude;
        private String imageUrl;
    }
}