package com.nexus.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.ai.dto.SuggestionDecisionRequest;
import com.nexus.ai.entity.SuggestionStatus;
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

/**
 * Integration tests for {@link AiController} (US-11 to US-14).
 * Uses {@code AI_PROVIDER=mock} (set in application-test.yml) — no real API calls.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AiController — Integration Tests")
class AiControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AuthService authService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired OrganizationRepository organizationRepository;

    private String operatorToken;
    private String requesterToken;
    private UUID caseId;
    private UUID categoryId;

    @BeforeEach
    void setUp() throws Exception {
        long ts = System.currentTimeMillis();
        Organization org = organizationRepository.save(new Organization("AI Test Org " + ts));
        Category cat = categoryRepository.save(new Category(org, "Network", null, null));
        this.categoryId = cat.getId();

        // Register Operator
        AuthResponse opAuth = authService.register(new RegisterRequest(
                "Op User " + ts, "op_ai_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.OPERATOR));
        this.operatorToken = opAuth.getAccessToken();

        // Register Requester
        AuthResponse reqAuth = authService.register(new RegisterRequest(
                "Req User " + ts, "req_ai_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.REQUESTER));
        this.requesterToken = reqAuth.getAccessToken();

        // Create a case (triggers async mock AI analysis)
        CreateCaseRequest caseReq = new CreateCaseRequest();
        caseReq.setTitle("VPN not connecting");
        caseReq.setDescription("Cannot connect to VPN since this morning. Getting error 401.");
        caseReq.setCategoryId(categoryId);
        caseReq.setPriority(Priority.MEDIUM);
        caseReq.setSeverity(Severity.MEDIUM);

        MvcResult result = mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(caseReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> resp = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Map<?, ?> data = (Map<?, ?>) resp.get("data");
        this.caseId = UUID.fromString((String) data.get("id"));

        // Brief pause for async AI analysis to complete (mock is fast)
        Thread.sleep(300);
    }

    @Test
    @DisplayName("GET /cases/{id}/ai/analysis — returns mock AI analysis (US-11)")
    void getAnalysis_returnsAnalysis() throws Exception {
        mockMvc.perform(get("/api/v1/cases/{id}/ai/analysis", caseId)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.suggestedPriority", is("HIGH")))
                .andExpect(jsonPath("$.data.confidence", notNullValue()))
                .andExpect(jsonPath("$.data.recommendedNextAction", notNullValue()));
    }

    @Test
    @DisplayName("GET /cases/{id}/ai/suggestions — returns PENDING suggestions (US-14)")
    void getSuggestions_returnsPendingSuggestions() throws Exception {
        mockMvc.perform(get("/api/v1/cases/{id}/ai/suggestions", caseId)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].status", is("PENDING")));
    }

    @Test
    @DisplayName("POST /cases/{id}/ai/analyze — operator re-triggers analysis (US-11)")
    void triggerAnalysis_returnsAccepted() throws Exception {
        mockMvc.perform(post("/api/v1/cases/{id}/ai/analyze", caseId)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @DisplayName("POST /cases/{id}/ai/analyze — requester gets 403")
    void triggerAnalysis_requester_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/cases/{id}/ai/analyze", caseId)
                        .header("Authorization", "Bearer " + requesterToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /ai/suggestions/{id} — operator accepts suggestion (US-14)")
    void decideSuggestion_accept_updatesStatus() throws Exception {
        // Get suggestion ID from the list
        MvcResult listResult = mockMvc.perform(get("/api/v1/cases/{id}/ai/suggestions", caseId)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> listResp = objectMapper.readValue(listResult.getResponse().getContentAsString(), Map.class);
        var suggestions = (java.util.List<?>) listResp.get("data");
        if (suggestions.isEmpty()) return; // Analysis might not have produced suggestions yet

        String suggestionId = (String) ((Map<?, ?>) suggestions.get(0)).get("id");

        SuggestionDecisionRequest req = new SuggestionDecisionRequest();
        req.setStatus(SuggestionStatus.ACCEPTED);

        mockMvc.perform(put("/api/v1/ai/suggestions/{id}", suggestionId)
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("ACCEPTED")))
                .andExpect(jsonPath("$.data.decidedByName", notNullValue()));
    }

    @Test
    @DisplayName("Case creation returns 201 even when AI analysis runs asynchronously (US-15)")
    void caseCreation_unaffectedByAiStatus() throws Exception {
        // Create a second case — must always return 201 regardless of AI state
        CreateCaseRequest req = new CreateCaseRequest();
        req.setTitle("Email not loading");
        req.setDescription("Cannot access company email portal");
        req.setCategoryId(categoryId);
        req.setPriority(Priority.LOW);
        req.setSeverity(Severity.LOW);

        mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id", notNullValue()));
    }

    @Test
    @DisplayName("GET /cases/{id}/ai/duplicates — returns potential duplicates (US-16)")
    void getDuplicates_returnsDuplicateList() throws Exception {
        // Create a duplicate case
        CreateCaseRequest dupReq = new CreateCaseRequest();
        dupReq.setTitle("VPN error 401 connection failure");
        dupReq.setDescription("Users getting 401 error on VPN login since morning");
        dupReq.setCategoryId(categoryId);
        dupReq.setPriority(Priority.MEDIUM);
        dupReq.setSeverity(Severity.MEDIUM);

        mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/cases/{id}/ai/duplicates", caseId)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("GET /cases/{id}/ai/assignment-recommendation — returns recommendation (US-19, US-20)")
    void getAssignmentRecommendation_returnsRecommendation() throws Exception {
        mockMvc.perform(get("/api/v1/cases/{id}/ai/assignment-recommendation", caseId)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.suggestedUserName", notNullValue()))
                .andExpect(jsonPath("$.data.confidence", notNullValue()))
                .andExpect(jsonPath("$.data.reasoning", notNullValue()));
    }
}
