package com.foodflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class CartDtos {

    @Data
    public static class AddToCartRequest {
        @NotNull private Long foodItemId;
        @Min(1) private int quantity;
    }

    @Data
    public static class UpdateCartItemRequest {
        @Min(1) private int quantity;
    }

    @Data
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long cartItemId;
        private Long foodItemId;
        private String foodName;
        private BigDecimal price;
        private int quantity;
        private BigDecimal subtotal;
    }

    @Data
    @AllArgsConstructor
    public static class CartResponse {
        private Long restaurantId;
        private String restaurantName;
        private List<CartItemResponse> items;
        private BigDecimal itemsTotal;
    }
}