package com.foodflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

public class MenuDtos {

    @Data
    public static class CreateCategoryRequest {
        @NotBlank private String name;
    }

    @Data
    @AllArgsConstructor
    public static class CategoryResponse {
        private Long id;
        private String name;
    }

    @Data
    public static class CreateFoodItemRequest {
        @NotBlank private String name;
        private String description;
        @NotNull @Positive private BigDecimal price;
        private boolean veg;
        @NotNull private Long categoryId;
        private String imageUrl;
    }

    @Data
    @AllArgsConstructor
    public static class FoodItemResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private boolean veg;
        private boolean available;
        private String categoryName;
        private String imageUrl;
    }

    @Data
    public static class UpdateAvailabilityRequest {
        @NotNull private Boolean available;
    }

    @Data
    public static class CreateIngredientRequest {
        @NotBlank private String name;
        @NotBlank private String unit;
        @NotNull @Positive private Double quantityAvailable;
    }

    @Data
    public static class LinkIngredientRequest {
        @NotNull private Long ingredientId;
        @NotNull @Positive private Double quantityRequired;
    }
}