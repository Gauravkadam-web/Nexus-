package com.nexus.sla.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.auth.dto.AuthResponse;
import com.nexus.auth.dto.RegisterRequest;
import com.nexus.auth.service.AuthService;
import com.nexus.casemanagement.dto.CreateCaseRequest;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.sla.dto.CreateSlaPolicyRequest;
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
@DisplayName("SlaController & AdminSlaPolicyController — Integration Tests")
class SlaControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AuthService authService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired OrganizationRepository organizationRepository;

    private String adminToken;
    private String operatorToken;
    private String requesterToken;
    private UUID caseId;
    private UUID categoryId;

    @BeforeEach
    void setUp() throws Exception {
        long ts = System.currentTimeMillis();
        Organization org = organizationRepository.save(new Organization("SLA Test Org " + ts));
        Category cat = categoryRepository.save(new Category(org, "Network SLA " + ts, null, null));
        categoryId = cat.getId();

        AuthResponse adminAuth = authService.register(new RegisterRequest(
                "Admin " + ts, "admin_sla_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.ADMIN));
        adminToken = adminAuth.getAccessToken();

        AuthResponse opAuth = authService.register(new RegisterRequest(
                "Op " + ts, "op_sla_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.OPERATOR));
        operatorToken = opAuth.getAccessToken();

        AuthResponse reqAuth = authService.register(new RegisterRequest(
                "Req " + ts, "req_sla_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.REQUESTER));
        requesterToken = reqAuth.getAccessToken();

        // Create a case
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("SLA Test Incident");
        req.setDescription("Network gateway unresponsive");
        req.setCategoryId(categoryId);
        req.setPriority(Priority.HIGH);
        req.setSeverity(Severity.HIGH);

        MvcResult result = mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> body = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Map<?, ?> data = (Map<?, ?>) body.get("data");
        caseId = UUID.fromString((String) data.get("id"));
    }

    @Test
    @DisplayName("GET /api/v1/cases/{id}/sla: returns 200 with SLA breakdown")
    void testGetCaseSla() throws Exception {
        mockMvc.perform(get("/api/v1/cases/" + caseId + "/sla")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.caseId", is(caseId.toString())))
                .andExpect(jsonPath("$.data.status", notNullValue()))
                .andExpect(jsonPath("$.data.responseDeadline", notNullValue()))
                .andExpect(jsonPath("$.data.resolutionDeadline", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/sla/at-risk: returns 200 with at-risk cases")
    void testGetAtRiskCases() throws Exception {
        mockMvc.perform(get("/api/v1/sla/at-risk")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", notNullValue()));
    }

    @Test
    @DisplayName("Admin SLA Policies CRUD: create, list, and delete policy")
    void testAdminSlaPolicyCrud() throws Exception {
        CreateSlaPolicyRequest policyReq = new CreateSlaPolicyRequest(categoryId, Priority.URGENT, 10, 60);

        MvcResult createRes = mockMvc.perform(post("/api/v1/admin/sla-policies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.responseTimeMinutes", is(10)))
                .andExpect(jsonPath("$.data.resolutionTimeMinutes", is(60)))
                .andReturn();

        Map<?, ?> body = objectMapper.readValue(createRes.getResponse().getContentAsString(), Map.class);
        Map<?, ?> data = (Map<?, ?>) body.get("data");
        String policyId = (String) data.get("id");

        mockMvc.perform(get("/api/v1/admin/sla-policies")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(delete("/api/v1/admin/sla-policies/" + policyId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
