package com.foodflow.service;

import com.foodflow.dto.NotificationDtos.NotificationResponse;
import com.foodflow.entity.Notification;
import com.foodflow.entity.NotificationType;
import com.foodflow.entity.User;
import com.foodflow.exception.ApiException;
import com.foodflow.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Called internally by OrderService at each status milestone — not exposed as a public API
    public void notify(User user, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .user(user).title(title).message(message).type(type).isRead(false).build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(User user) {
        return notificationRepository.findByUserIdOrderByIdDesc(user.getId()).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> ApiException.notFound("Notification not found"));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw ApiException.forbidden("This notification does not belong to you");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getTitle(), n.getMessage(), n.getType(), n.isRead(), n.getCreatedAt());
    }
}