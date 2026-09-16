package com.nexus.casemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.auth.dto.AuthResponse;
import com.nexus.auth.dto.RegisterRequest;
import com.nexus.auth.service.AuthService;
import com.nexus.casemanagement.dto.CreateCaseRequest;
import com.nexus.casemanagement.dto.UpdateCaseStatusRequest;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.user.entity.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private String requesterToken;
    private String operatorToken;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        Organization org = organizationRepository.save(new Organization("Case Test Org " + System.currentTimeMillis()));
        Category cat = categoryRepository.save(new Category(org, "Hardware Issue", null, null));
        this.categoryId = cat.getId();

        // Register Requester
        String reqEmail = "req_" + System.currentTimeMillis() + "@nexus.com";
        AuthResponse reqAuth = authService.register(new RegisterRequest(
                "Requester One", reqEmail, "password123", org.getName(), RoleType.REQUESTER
        ));
        this.requesterToken = reqAuth.getAccessToken();

        // Register Operator
        String opEmail = "op_" + System.currentTimeMillis() + "@nexus.com";
        AuthResponse opAuth = authService.register(new RegisterRequest(
                "Operator One", opEmail, "password123", org.getName(), RoleType.OPERATOR
        ));
        this.operatorToken = opAuth.getAccessToken();
    }

    @Test
    void createAndManageCaseFlow_ShouldSucceed() throws Exception {
        // 1. US-1: Requester creates a case
        CreateCaseRequest createReq = new CreateCaseRequest(
                "Laptop screen flickering",
                "Screen flickers whenever connected to external monitor",
                categoryId,
                null,
                Severity.HIGH,
                Priority.HIGH,
                "Building 4, Desk 202"
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.caseNumber", notNullValue()))
                .andExpect(jsonPath("$.data.status", is("REPORTED")))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String caseIdStr = objectMapper.readTree(responseBody).get("data").get("id").asText();
        UUID caseId = UUID.fromString(caseIdStr);

        // 2. US-2: Requester views their submitted cases
        mockMvc.perform(get("/api/v1/cases/my")
                        .header("Authorization", "Bearer " + requesterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[0].title", is("Laptop screen flickering")));

        // 3. US-4: Operator updates case status (REPORTED -> UNDERSTOOD)
        UpdateCaseStatusRequest updateReq = new UpdateCaseStatusRequest(CaseStatus.UNDERSTOOD, "Triage confirmed");
        mockMvc.perform(patch("/api/v1/cases/" + caseId + "/status")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("UNDERSTOOD")));

        // 4. Invalid status transition (UNDERSTOOD cannot jump straight to CLOSED)
        UpdateCaseStatusRequest invalidReq = new UpdateCaseStatusRequest(CaseStatus.CLOSED, "Jump to closed");
        mockMvc.perform(patch("/api/v1/cases/" + caseId + "/status")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
