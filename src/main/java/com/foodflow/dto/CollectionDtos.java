package com.foodflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

public class CollectionDtos {

    @Data
    @AllArgsConstructor
    public static class FavoriteResponse {
        private Long restaurantId;
        private String restaurantName;
        private String imageUrl;
        private String cuisineType;
        private Double averageRating;
    }

    @Data
    @AllArgsConstructor
    public static class LikedDishResponse {
        private Long foodItemId;
        private String foodItemName;
        private String imageUrl;
        private BigDecimal price;
        private String restaurantName;
        private Long restaurantId;
        private Integer stars;
    }
}