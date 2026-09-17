package com.nexus.audit.controller;

import com.nexus.audit.dto.AuditFilterRequest;
import com.nexus.audit.dto.AuditLogResponse;
import com.nexus.audit.entity.AuditLog;
import com.nexus.audit.repository.AuditLogRepository;
import com.nexus.audit.service.AuditService;
import com.nexus.common.response.ApiResponse;
import com.nexus.organization.entity.Organization;
import com.nexus.user.entity.Role;
import com.nexus.user.entity.RoleType;
import com.nexus.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditController — Unit Tests")
class AuditControllerTest {

    @Mock private AuditLogRepository auditLogRepository;

    private AuditController auditController;

    private User operator;
    private Organization org;

    @BeforeEach
    void setUp() {
        AuditService auditService = new AuditService(auditLogRepository);
        auditController = new AuditController(auditService);

        org = new Organization("Test Corp");
        org.setId(UUID.randomUUID());

        operator = new User(org, "Alice Operator", "alice@test.com", "hash");
        operator.setId(UUID.randomUUID());
        Role opRole = new Role(RoleType.OPERATOR);
        operator.setRoles(Set.of(opRole));
    }

    @Test
    @DisplayName("US-33: Search audit logs returns paginated response")
    @SuppressWarnings("unchecked")
    void searchAuditLogs_success() {
        AuditLog log = new AuditLog("CASE", UUID.randomUUID(), "STATUS_CHANGE", operator, "Alice", "OPERATOR", "REPORTED", "IN_PROGRESS", "USER", "127.0.0.1");
        Pageable pageable = PageRequest.of(0, 10);

        when(auditLogRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(log), pageable, 1));

        ResponseEntity<ApiResponse<Page<AuditLogResponse>>> response =
                auditController.searchAuditLogs(new AuditFilterRequest(), pageable);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("US-33: Get case audit trail returns paginated case logs")
    void getCaseAuditTrail_success() {
        UUID caseId = UUID.randomUUID();
        AuditLog log = new AuditLog("CASE", caseId, "STATUS_CHANGE", operator, "Alice", "OPERATOR", "REPORTED", "IN_PROGRESS", "USER", "127.0.0.1");
        Pageable pageable = PageRequest.of(0, 10);

        when(auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("CASE", caseId, pageable))
                .thenReturn(new PageImpl<>(List.of(log), pageable, 1));

        ResponseEntity<ApiResponse<Page<AuditLogResponse>>> response =
                auditController.getCaseAuditTrail(caseId, pageable);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getContent()).hasSize(1);
    }

    @Test
    @DisplayName("US-33: Get case audit timeline returns full ordered list")
    void getCaseAuditTimeline_success() {
        UUID caseId = UUID.randomUUID();
        AuditLog log = new AuditLog("CASE", caseId, "STATUS_CHANGE", operator, "Alice", "OPERATOR", "REPORTED", "IN_PROGRESS", "USER", "127.0.0.1");

        when(auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc("CASE", caseId))
                .thenReturn(List.of(log));

        ResponseEntity<ApiResponse<List<AuditLogResponse>>> response =
                auditController.getCaseAuditTimeline(caseId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
    }
}
