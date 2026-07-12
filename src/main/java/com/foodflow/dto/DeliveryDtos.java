package com.foodflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

public class DeliveryDtos {

    @Data
    public static class LocationRequest {
        @NotNull private Double latitude;
        @NotNull private Double longitude;
    }

    @Data
    @AllArgsConstructor
    public static class AgentProfileResponse {
        private Long id;
        private boolean online;
        private boolean busy;
        private Double latitude;
        private Double longitude;
        private BigDecimal totalEarnings;
    }
}