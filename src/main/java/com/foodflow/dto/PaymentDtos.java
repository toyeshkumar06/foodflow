package com.foodflow.dto;

import com.foodflow.entity.PaymentMethod;
import com.foodflow.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDtos {

    @Data
    public static class InitiatePaymentRequest {
        @NotNull private PaymentMethod method;
    }

    @Data
    @AllArgsConstructor
    public static class PaymentResponse {
        private Long id;
        private Long orderId;
        private PaymentMethod method;
        private PaymentStatus status;
        private BigDecimal amount;
        private String transactionRef;
        private LocalDateTime createdAt;
    }
}