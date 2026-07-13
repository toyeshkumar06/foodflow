package com.foodflow.dto;

import com.foodflow.entity.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CouponDtos {

    @Data
    public static class CreateCouponRequest {
        @NotBlank private String code;
        private String description;
        @NotNull private DiscountType discountType;
        @NotNull @Positive private BigDecimal discountValue;
        private BigDecimal minBillAmount;
        private BigDecimal maxDiscountAmount;
        @NotNull private String expiryDate; // send as "2026-12-31"
        private Integer usageLimit;
        private boolean firstOrderOnly;
        private Long restaurantId; // optional
    }

    @Data
    @AllArgsConstructor
    public static class CouponResponse {
        private Long id;
        private String code;
        private String description;
        private DiscountType discountType;
        private BigDecimal discountValue;
        private BigDecimal minBillAmount;
        private BigDecimal maxDiscountAmount;
        private LocalDate expiryDate;
        private Integer usageLimit;
        private Integer usageCount;
        private boolean firstOrderOnly;
        private Long restaurantId;
        private boolean active;
    }
}