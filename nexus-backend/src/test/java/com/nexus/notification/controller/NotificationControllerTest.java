package com.nexus.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.auth.dto.AuthResponse;
import com.nexus.auth.dto.RegisterRequest;
import com.nexus.auth.service.AuthService;
import com.nexus.notification.entity.Notification;
import com.nexus.notification.entity.NotificationType;
import com.nexus.notification.repository.NotificationRepository;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.user.entity.RoleType;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("NotificationController — Integration Tests")
class NotificationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AuthService authService;
    @Autowired OrganizationRepository organizationRepository;
    @Autowired UserRepository userRepository;
    @Autowired NotificationRepository notificationRepository;

    private String userToken;
    private User user;
    private Notification testNotification;

    @BeforeEach
    void setUp() throws Exception {
        long ts = System.currentTimeMillis();
        Organization org = organizationRepository.save(new Organization("Notif Test Org " + ts));

        AuthResponse auth = authService.register(new RegisterRequest(
                "Notif User " + ts, "notif_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.OPERATOR));
        userToken = auth.getAccessToken();

        user = userRepository.findByEmail("notif_" + ts + "@nexus.com").orElseThrow();

        testNotification = notificationRepository.save(new Notification(
                user, null, NotificationType.SLA_WARNING, "SLA Warning", "Case deadline approaching"
        ));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/my: returns paginated list of user notifications")
    void testGetMyNotifications() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/my")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].title", is("SLA Warning")));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/unread-count: returns correct unread count")
    void testGetUnreadCount() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unreadCount", is(1)));
    }

    @Test
    @DisplayName("PATCH /api/v1/notifications/{id}/read: marks notification as read")
    void testMarkAsRead() throws Exception {
        mockMvc.perform(patch("/api/v1/notifications/" + testNotification.getId() + "/read")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.read", is(true)));
    }

    @Test
    @DisplayName("PATCH /api/v1/notifications/read-all: marks all notifications as read")
    void testMarkAllAsRead() throws Exception {
        mockMvc.perform(patch("/api/v1/notifications/read-all")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unreadCount", is(0)));
    }
}
