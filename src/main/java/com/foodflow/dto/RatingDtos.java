package com.foodflow.dto;

import com.foodflow.entity.RatingTargetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

public class RatingDtos {

    @Data
    public static class RateRequest {
        @NotNull @Min(1) @Max(5) private Integer stars;
        private String reviewText;
    }

    @Data
    @AllArgsConstructor
    public static class RatingResponse {
        private Long id;
        private RatingTargetType targetType;
        private Long targetId;
        private Integer stars;
        private String reviewText;
        private String customerName;
        private LocalDateTime createdAt;
    }
}