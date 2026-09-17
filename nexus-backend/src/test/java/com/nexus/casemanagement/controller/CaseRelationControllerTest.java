package com.nexus.casemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.auth.dto.AuthResponse;
import com.nexus.auth.dto.RegisterRequest;
import com.nexus.auth.service.AuthService;
import com.nexus.casemanagement.dto.CreateCaseRelationRequest;
import com.nexus.casemanagement.dto.CreateCaseRequest;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.RelationType;
import com.nexus.casemanagement.entity.Severity;
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
@DisplayName("CaseRelationController — Integration Tests")
class CaseRelationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AuthService authService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired OrganizationRepository organizationRepository;

    private String operatorToken;
    private String requesterToken;
    private UUID case1Id;
    private UUID case2Id;

    @BeforeEach
    void setUp() throws Exception {
        long ts = System.currentTimeMillis();
        Organization org = organizationRepository.save(new Organization("Relation Test Org " + ts));
        Category cat = categoryRepository.save(new Category(org, "Hardware " + ts, null, null));

        AuthResponse opAuth = authService.register(new RegisterRequest(
                "Op Rel " + ts, "op_rel_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.OPERATOR));
        this.operatorToken = opAuth.getAccessToken();

        AuthResponse reqAuth = authService.register(new RegisterRequest(
                "Req Rel " + ts, "req_rel_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.REQUESTER));
        this.requesterToken = reqAuth.getAccessToken();

        // Create Case 1
        CreateCaseRequest c1 = new CreateCaseRequest();
        c1.setTitle("Laptop screen flickering");
        c1.setDescription("Dell latitude screen turns black");
        c1.setCategoryId(cat.getId());
        c1.setPriority(Priority.HIGH);
        c1.setSeverity(Severity.HIGH);

        MvcResult r1 = mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c1)))
                .andExpect(status().isCreated())
                .andReturn();
        Map<?, ?> resp1 = objectMapper.readValue(r1.getResponse().getContentAsString(), Map.class);
        this.case1Id = UUID.fromString((String) ((Map<?, ?>) resp1.get("data")).get("id"));

        // Create Case 2
        CreateCaseRequest c2 = new CreateCaseRequest();
        c2.setTitle("Dell display blacking out");
        c2.setDescription("External and internal display issue on Dell");
        c2.setCategoryId(cat.getId());
        c2.setPriority(Priority.MEDIUM);
        c2.setSeverity(Severity.MEDIUM);

        MvcResult r2 = mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c2)))
                .andExpect(status().isCreated())
                .andReturn();
        Map<?, ?> resp2 = objectMapper.readValue(r2.getResponse().getContentAsString(), Map.class);
        this.case2Id = UUID.fromString((String) ((Map<?, ?>) resp2.get("data")).get("id"));
    }

    @Test
    @DisplayName("POST /cases/{id}/relations — operator successfully links duplicate cases (US-17)")
    void linkCases_operator_returns201() throws Exception {
        CreateCaseRelationRequest req = new CreateCaseRelationRequest(case2Id, RelationType.DUPLICATE, "Same Dell GPU issue");

        mockMvc.perform(post("/api/v1/cases/{id}/relations", case1Id)
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.relationType", is("DUPLICATE")))
                .andExpect(jsonPath("$.data.linkedByName", notNullValue()));
    }

    @Test
    @DisplayName("GET /cases/{id}/relations — lists relations for a case")
    void getRelations_returns200() throws Exception {
        CreateCaseRelationRequest req = new CreateCaseRelationRequest(case2Id, RelationType.RELATED, "Hardware batch");
        mockMvc.perform(post("/api/v1/cases/{id}/relations", case1Id)
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/cases/{id}/relations", case1Id)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("POST /cases/{id}/relations — Master Incident linking and retrieving children (US-18)")
    void masterIncident_linkAndGetChildren() throws Exception {
        CreateCaseRelationRequest req = new CreateCaseRelationRequest(case2Id, RelationType.MASTER_INCIDENT, "Major incident tracking");
        mockMvc.perform(post("/api/v1/cases/{id}/relations", case1Id)
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/cases/{id}/master-incident/children", case1Id)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].relationType", is("MASTER_INCIDENT")));
    }
}
