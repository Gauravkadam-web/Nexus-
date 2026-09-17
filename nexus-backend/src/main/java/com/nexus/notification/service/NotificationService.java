package com.nexus.notification.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.notification.dto.NotificationResponse;
import com.nexus.notification.entity.Notification;
import com.nexus.notification.entity.NotificationType;
import com.nexus.notification.repository.NotificationRepository;
import com.nexus.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Creates an in-app notification for a user.
     */
    public Notification createNotification(User user, Case caseEntity, NotificationType type, String title, String message) {
        if (user == null) {
            log.warn("Cannot create notification for null user");
            return null;
        }
        Notification notification = new Notification(user, caseEntity, type, title, message);
        Notification saved = notificationRepository.save(notification);
        log.info("Created notification [{}] for user [{}] regarding case [{}]", type, user.getId(),
                caseEntity != null ? caseEntity.getCaseNumber() : "N/A");
        return saved;
    }

    /**
     * Retrieves paginated notifications for the authenticated user.
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationResponse::fromEntity);
    }

    /**
     * Gets unread notification count for a user.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    /**
     * Marks a specific notification as read.
     */
    public NotificationResponse markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(saved);
    }

    /**
     * Marks all notifications as read for a user.
     */
    public void markAllAsRead(UUID userId) {
        Page<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged());
        unread.forEach(n -> {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }
}
