package com.nexus.problem.controller;

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
import com.nexus.problem.dto.CreateProblemRequest;
import com.nexus.problem.dto.LinkIncidentRequest;
import com.nexus.problem.dto.UpdateProblemRequest;
import com.nexus.problem.entity.ProblemStatus;
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
@DisplayName("ProblemController — Integration Tests")
class ProblemControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AuthService authService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired OrganizationRepository organizationRepository;

    private String managerToken;
    private String operatorToken;
    private UUID caseId;

    @BeforeEach
    void setUp() throws Exception {
        long ts = System.currentTimeMillis();
        Organization org = organizationRepository.save(new Organization("Problem Test Org " + ts));
        Category cat = categoryRepository.save(new Category(org, "Infrastructure " + ts, null, null));

        AuthResponse mgrAuth = authService.register(new RegisterRequest(
                "Mgr " + ts, "mgr_prb_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.MANAGER));
        managerToken = mgrAuth.getAccessToken();

        AuthResponse opAuth = authService.register(new RegisterRequest(
                "Op " + ts, "op_prb_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.OPERATOR));
        operatorToken = opAuth.getAccessToken();

        CreateCaseRequest caseReq = new CreateCaseRequest();
        caseReq.setTitle("Auth Gateway 502 Errors " + ts);
        caseReq.setDescription("Gateway timed out connecting to backend auth service.");
        caseReq.setCategoryId(cat.getId());
        caseReq.setPriority(Priority.HIGH);
        caseReq.setSeverity(Severity.HIGH);

        MvcResult result = mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(caseReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> resp = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Map<?, ?> data = (Map<?, ?>) resp.get("data");
        caseId = UUID.fromString((String) data.get("id"));
    }

    @Test
    @DisplayName("US-28: Create Problem, Link Incident, and Update Problem lifecycle")
    void problemManagementLifecycle() throws Exception {
        // 1. Create Problem
        CreateProblemRequest createReq = new CreateProblemRequest();
        createReq.setTitle("Auth Service Connection Exhaustion");
        createReq.setSuspectedRootCause("Hikari connection pool saturation causing HTTP 502 errors.");

        MvcResult createResult = mockMvc.perform(post("/api/v1/problems")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Auth Service Connection Exhaustion"))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andReturn();

        Map<?, ?> resp = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
        Map<?, ?> data = (Map<?, ?>) resp.get("data");
        String problemIdStr = (String) data.get("id");

        // 2. Link Incident to Problem
        LinkIncidentRequest linkReq = new LinkIncidentRequest();
        linkReq.setCaseId(caseId);

        mockMvc.perform(post("/api/v1/problems/" + problemIdStr + "/incidents")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.linkedIncidentCount", greaterThanOrEqualTo(1)));

        // 3. Update Problem (Resolve)
        UpdateProblemRequest updateReq = new UpdateProblemRequest();
        updateReq.setStatus(ProblemStatus.RESOLVED);
        updateReq.setConfirmedRootCause("Max connections pool capped at 10 instead of 50 in production config.");
        updateReq.setCorrectiveAction("Updated connection pool configuration to 50 connections with idle timeout 30s.");

        mockMvc.perform(put("/api/v1/problems/" + problemIdStr)
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("RESOLVED"))
                .andExpect(jsonPath("$.data.confirmedRootCause").value("Max connections pool capped at 10 instead of 50 in production config."));

        // 4. Check recurring patterns endpoint
        mockMvc.perform(get("/api/v1/problems/recurring-patterns")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()));
    }
}
