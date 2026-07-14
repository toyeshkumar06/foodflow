package com.foodflow.controller;

import com.foodflow.dto.NotificationDtos.NotificationResponse;
import com.foodflow.entity.User;
import com.foodflow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getMyNotifications(@AuthenticationPrincipal User user) {
        return notificationService.getMyNotifications(user);
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, @AuthenticationPrincipal User user) {
        notificationService.markAsRead(id, user);
    }
}