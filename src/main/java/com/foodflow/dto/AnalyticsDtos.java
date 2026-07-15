package com.foodflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AnalyticsDtos {

    @Data
    @AllArgsConstructor
    public static class TopItem {
        private String name;
        private long quantitySold;
    }

    @Data
    @AllArgsConstructor
    public static class DailySales {
        private LocalDate date;
        private BigDecimal revenue;
        private long orderCount;
    }

    @Data
    @AllArgsConstructor
    public static class AdminOverviewResponse {
        private BigDecimal totalRevenue;
        private long totalOrders;
        private String mostPopularRestaurant;
        private String mostPopularFood;
        private String mostActiveCustomer;
    }

    @Data
    @AllArgsConstructor
    public static class RestaurantAnalyticsResponse {
        private BigDecimal totalRevenue;
        private long totalOrders;
        private List<TopItem> topSellingItems;
        private List<DailySales> last7Days;
    }
}