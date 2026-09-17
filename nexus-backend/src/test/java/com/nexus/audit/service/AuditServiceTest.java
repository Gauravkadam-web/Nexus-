package com.nexus.audit.service;

import com.nexus.audit.dto.AuditFilterRequest;
import com.nexus.audit.dto.AuditLogResponse;
import com.nexus.audit.entity.AuditLog;
import com.nexus.audit.repository.AuditLogRepository;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditService — Unit Tests")
class AuditServiceTest {

    @Mock private AuditLogRepository auditLogRepository;

    private AuditService auditService;

    private User operator;
    private Organization org;

    @BeforeEach
    void setUp() {
        auditService = new AuditService(auditLogRepository);

        org = new Organization("Test Corp");
        org.setId(UUID.randomUUID());

        operator = new User(org, "Alice Operator", "alice@test.com", "hash");
        operator.setId(UUID.randomUUID());
        Role opRole = new Role(RoleType.OPERATOR);
        operator.setRoles(Set.of(opRole));
    }

    @Test
    @DisplayName("US-33: logEvent saves immutable audit log entry")
    void logEvent_success() {
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

        UUID caseId = UUID.randomUUID();
        AuditLog saved = auditService.logEvent(
                "CASE", caseId, "STATUS_CHANGE", operator,
                "REPORTED", "IN_PROGRESS", "WEB", "192.168.1.1"
        );

        assertThat(saved).isNotNull();
        assertThat(saved.getEntityType()).isEqualTo("CASE");
        assertThat(saved.getEntityId()).isEqualTo(caseId);
        assertThat(saved.getAction()).isEqualTo("STATUS_CHANGE");
        assertThat(saved.getActorName()).isEqualTo("Alice Operator");
        assertThat(saved.getOldValue()).isEqualTo("REPORTED");
        assertThat(saved.getNewValue()).isEqualTo("IN_PROGRESS");
        assertThat(saved.getSource()).isEqualTo("WEB");
        assertThat(saved.getIpAddress()).isEqualTo("192.168.1.1");
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("US-33: getCaseAuditTrail returns paginated audit entries")
    void getCaseAuditTrail_success() {
        UUID caseId = UUID.randomUUID();
        AuditLog log = new AuditLog("CASE", caseId, "STATUS_CHANGE", operator, "Alice", "OPERATOR", "REPORTED", "IN_PROGRESS", "USER", "127.0.0.1");
        Pageable pageable = PageRequest.of(0, 10);

        when(auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc("CASE", caseId, pageable))
                .thenReturn(new PageImpl<>(List.of(log), pageable, 1));

        Page<AuditLogResponse> result = auditService.getCaseAuditTrail(caseId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAction()).isEqualTo("STATUS_CHANGE");
    }

    @Test
    @DisplayName("US-33: getCaseAuditTimeline returns chronological audit logs")
    void getCaseAuditTimeline_success() {
        UUID caseId = UUID.randomUUID();
        AuditLog log1 = new AuditLog("CASE", caseId, "CASE_CREATED", operator, "Alice", "OPERATOR", null, "REPORTED", "USER", "127.0.0.1");
        AuditLog log2 = new AuditLog("CASE", caseId, "STATUS_CHANGE", operator, "Alice", "OPERATOR", "REPORTED", "UNDERSTOOD", "USER", "127.0.0.1");

        when(auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc("CASE", caseId))
                .thenReturn(List.of(log1, log2));

        List<AuditLogResponse> timeline = auditService.getCaseAuditTimeline(caseId);

        assertThat(timeline).hasSize(2);
        assertThat(timeline.get(0).getAction()).isEqualTo("CASE_CREATED");
        assertThat(timeline.get(1).getAction()).isEqualTo("STATUS_CHANGE");
    }

    @Test
    @DisplayName("US-33: searchAuditLogs with filter executes specification query")
    @SuppressWarnings("unchecked")
    void searchAuditLogs_success() {
        AuditFilterRequest filter = new AuditFilterRequest();
        filter.setEntityType("CASE");
        filter.setAction("STATUS_CHANGE");

        AuditLog logEntry = new AuditLog("CASE", UUID.randomUUID(), "STATUS_CHANGE", operator, "Alice", "OPERATOR", "REPORTED", "IN_PROGRESS", "USER", "127.0.0.1");
        Pageable pageable = PageRequest.of(0, 20);

        when(auditLogRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(logEntry), pageable, 1));

        Page<AuditLogResponse> result = auditService.searchAuditLogs(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
