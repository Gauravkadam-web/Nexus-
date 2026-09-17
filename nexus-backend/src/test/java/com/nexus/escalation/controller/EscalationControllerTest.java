package com.nexus.escalation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.auth.dto.AuthResponse;
import com.nexus.auth.dto.RegisterRequest;
import com.nexus.auth.service.AuthService;
import com.nexus.casemanagement.dto.CreateCaseRequest;
import com.nexus.casemanagement.dto.UpdateCaseStatusRequest;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import com.nexus.escalation.dto.CreateEscalationRuleRequest;
import com.nexus.escalation.dto.EscalateCaseRequest;
import com.nexus.escalation.entity.EscalationConditionType;
import com.nexus.escalation.entity.EscalationLevel;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.user.entity.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("EscalationController & AdminEscalationRuleController — Integration Tests")
class EscalationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AuthService authService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired OrganizationRepository organizationRepository;

    private String adminToken;
    private String operatorToken;
    private String leadToken;
    private UUID caseId;
    private UUID categoryId;

    @BeforeEach
    void setUp() throws Exception {
        long ts = System.currentTimeMillis();
        Organization org = organizationRepository.save(new Organization("Escalation Test Org " + ts));
        Category cat = categoryRepository.save(new Category(org, "Escalation Cat " + ts, null, null));
        categoryId = cat.getId();

        AuthResponse adminAuth = authService.register(new RegisterRequest(
                "Admin Esc " + ts, "admin_esc_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.ADMIN));
        adminToken = adminAuth.getAccessToken();

        AuthResponse opAuth = authService.register(new RegisterRequest(
                "Op Esc " + ts, "op_esc_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.OPERATOR));
        operatorToken = opAuth.getAccessToken();

        AuthResponse leadAuth = authService.register(new RegisterRequest(
                "Lead Esc " + ts, "lead_esc_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.TEAM_LEAD));
        leadToken = leadAuth.getAccessToken();

        AuthResponse reqAuth = authService.register(new RegisterRequest(
                "Req Esc " + ts, "req_esc_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.REQUESTER));

        // Create case
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("Critical Outage");
        req.setDescription("System completely down");
        req.setCategoryId(categoryId);
        req.setPriority(Priority.URGENT);
        req.setSeverity(Severity.CRITICAL);

        MvcResult result = mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + reqAuth.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Map<?, ?> data = (Map<?, ?>) body.get("data");
        caseId = UUID.fromString((String) data.get("id"));

        // Progress case to UNDERSTOOD -> ASSIGNED -> INVESTIGATING so it can be escalated
        mockMvc.perform(patch("/api/v1/cases/" + caseId + "/status")
                .header("Authorization", "Bearer " + operatorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateCaseStatusRequest(CaseStatus.UNDERSTOOD))));

        mockMvc.perform(patch("/api/v1/cases/" + caseId + "/status")
                .header("Authorization", "Bearer " + operatorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateCaseStatusRequest(CaseStatus.ASSIGNED))));

        mockMvc.perform(patch("/api/v1/cases/" + caseId + "/status")
                .header("Authorization", "Bearer " + operatorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateCaseStatusRequest(CaseStatus.INVESTIGATING))));
    }

    @Test
    @DisplayName("POST /api/v1/cases/{id}/escalate: escalates case and returns 201 Created")
    void testEscalateCase() throws Exception {
        EscalateCaseRequest escReq = new EscalateCaseRequest("Critical service blocked", EscalationLevel.TEAM_LEAD, null);

        mockMvc.perform(post("/api/v1/cases/" + caseId + "/escalate")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(escReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.data.escalationLevel", is("TEAM_LEAD")));
    }

    @Test
    @DisplayName("Admin Escalation Rules CRUD: create, list, and delete rule")
    void testAdminEscalationRuleCrud() throws Exception {
        CreateEscalationRuleRequest ruleReq = new CreateEscalationRuleRequest(
                "Urgent Incident Lead Rule", EscalationConditionType.HIGH_IMPACT_INCIDENT,
                null, EscalationLevel.TEAM_LEAD, categoryId, true
        );

        MvcResult createRes = mockMvc.perform(post("/api/v1/admin/escalation-rules")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ruleReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is("Urgent Incident Lead Rule")))
                .andReturn();

        Map<?, ?> body = objectMapper.readValue(createRes.getResponse().getContentAsString(), Map.class);
        Map<?, ?> data = (Map<?, ?>) body.get("data");
        String ruleId = (String) data.get("id");

        mockMvc.perform(get("/api/v1/admin/escalation-rules")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(delete("/api/v1/admin/escalation-rules/" + ruleId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
