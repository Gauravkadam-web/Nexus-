package com.nexus.notification.repository;

import com.nexus.notification.entity.Notification;
import com.nexus.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    long countByUserIdAndReadFalse(UUID userId);

    boolean existsByUserIdAndCaseEntityIdAndType(UUID userId, UUID caseId, NotificationType type);
}
