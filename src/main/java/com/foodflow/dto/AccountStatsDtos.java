package com.foodflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

public class AccountStatsDtos {

    @Data
    @AllArgsConstructor
    public static class OwnerQuickStatsResponse {
        private int todayOrders;
        private BigDecimal todayRevenue;
        private String topRatedItemName;
        private Double topRatedItemRating;
    }

    @Data
    @AllArgsConstructor
    public static class AgentQuickStatsResponse {
        private int deliveriesToday;
        private BigDecimal todayEarnings;
        private int totalDeliveries;
    }
}