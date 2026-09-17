package com.nexus.notification.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.notification.dto.NotificationResponse;
import com.nexus.notification.entity.Notification;
import com.nexus.notification.entity.NotificationType;
import com.nexus.notification.repository.NotificationRepository;
import com.nexus.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Case testCase;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Alice Operator");
        user.setEmail("alice@nexus.local");

        testCase = new Case();
        testCase.setId(UUID.randomUUID());
        testCase.setCaseNumber("NEX-20260917-0004");
    }

    @Test
    @DisplayName("createNotification: saves notification and returns entity")
    void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            n.setId(UUID.randomUUID());
            return n;
        });

        Notification notif = notificationService.createNotification(
                user, testCase, NotificationType.SLA_WARNING, "SLA Warning", "Case deadline approaching"
        );

        assertThat(notif).isNotNull();
        assertThat(notif.getTitle()).isEqualTo("SLA Warning");
        assertThat(notif.getType()).isEqualTo(NotificationType.SLA_WARNING);
        assertThat(notif.isRead()).isFalse();
    }

    @Test
    @DisplayName("getUserNotifications: returns paginated list of notifications")
    void testGetUserNotifications() {
        Notification notif = new Notification(user, testCase, NotificationType.CASE_ASSIGNED, "Case Assigned", "You were assigned");
        notif.setId(UUID.randomUUID());

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(eq(user.getId()), any()))
                .thenReturn(new PageImpl<>(List.of(notif)));

        Page<NotificationResponse> result = notificationService.getUserNotifications(user.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Case Assigned");
    }

    @Test
    @DisplayName("markAsRead: sets read to true")
    void testMarkAsRead() {
        Notification notif = new Notification(user, testCase, NotificationType.SLA_BREACH, "SLA Breach", "Deadline missed");
        notif.setId(UUID.randomUUID());

        when(notificationRepository.findById(notif.getId())).thenReturn(Optional.of(notif));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponse resp = notificationService.markAsRead(notif.getId(), user.getId());

        assertThat(resp.isRead()).isTrue();
    }
}
