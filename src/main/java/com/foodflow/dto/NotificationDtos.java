package com.foodflow.dto;

import com.foodflow.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

public class NotificationDtos {

    @Data
    @AllArgsConstructor
    public static class NotificationResponse {
        private Long id;
        private String title;
        private String message;
        private NotificationType type;
        private boolean isRead;
        private LocalDateTime createdAt;
    }
}