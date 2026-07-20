package com.foodflow.dto;

import com.foodflow.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDtos {

    @Data
    public static class PlaceOrderRequest {
        @NotNull private Long addressId;
        private String couponCode; // optional
    }

    @Data
    public static class UpdateOrderStatusRequest {
        @NotNull private OrderStatus status;
    }

    @Data
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long foodItemId;
        private String foodName;
        private BigDecimal price;
        private int quantity;
    }

    @Data
    @AllArgsConstructor
    public static class OrderResponse {
        private Long id;
        private String restaurantName;
        private List<OrderItemResponse> items;
        private BigDecimal itemsTotal;
        private BigDecimal deliveryCharge;
        private BigDecimal discountAmount;
        private String appliedCouponCode;
        private BigDecimal grandTotal;
        private OrderStatus status;
        private String deliveryAddressLine;
        private LocalDateTime createdAt;
        private Integer etaMinutes;
        private Double distanceKm;
        private BigDecimal surgeMultiplier;
        private String deliveryAgentName;
        private boolean agentConfirmed;
    }
}