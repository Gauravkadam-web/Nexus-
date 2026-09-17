package com.nexus.resolution.controller;

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
import com.nexus.resolution.dto.ConfirmResolutionRequest;
import com.nexus.resolution.dto.RejectResolutionRequest;
import com.nexus.resolution.dto.SubmitResolutionRequest;
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
@DisplayName("ResolutionController — Integration Tests")
class ResolutionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AuthService authService;
    @Autowired CategoryRepository categoryRepository;
    @Autowired OrganizationRepository organizationRepository;

    private String operatorToken;
    private String requesterToken;
    private UUID caseId;

    @BeforeEach
    void setUp() throws Exception {
        long ts = System.currentTimeMillis();
        Organization org = organizationRepository.save(new Organization("Resolution Test Org " + ts));
        Category cat = categoryRepository.save(new Category(org, "Hardware " + ts, null, null));

        AuthResponse opAuth = authService.register(new RegisterRequest(
                "Op " + ts, "op_res_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.OPERATOR));
        operatorToken = opAuth.getAccessToken();

        AuthResponse reqAuth = authService.register(new RegisterRequest(
                "Req " + ts, "req_res_" + ts + "@nexus.com", "password123",
                org.getName(), RoleType.REQUESTER));
        requesterToken = reqAuth.getAccessToken();

        // Create case
        CreateCaseRequest caseReq = new CreateCaseRequest();
        caseReq.setTitle("Monitor Flickering " + ts);
        caseReq.setDescription("External screen keeps losing HDMI signal.");
        caseReq.setCategoryId(cat.getId());
        caseReq.setPriority(Priority.MEDIUM);
        caseReq.setSeverity(Severity.LOW);

        MvcResult result = mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(caseReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> resp = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Map<?, ?> data = (Map<?, ?>) resp.get("data");
        caseId = UUID.fromString((String) data.get("id"));

        // Assign operator to case to move to ASSIGNED
        mockMvc.perform(post("/api/v1/cases/" + caseId + "/assign")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("assignedUserId", opAuth.getUser().getId().toString()))))
                .andExpect(status().isOk());

        // Move to INVESTIGATING
        mockMvc.perform(patch("/api/v1/cases/" + caseId + "/status")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "INVESTIGATING"))))
                .andExpect(status().isOk());
    }



    @Test
    @DisplayName("US-26 & US-27: Full Resolution Lifecycle: Submit -> Get -> Confirm")
    void fullResolutionLifecycle_confirm() throws Exception {
        // 1. Operator submits resolution
        SubmitResolutionRequest submitReq = new SubmitResolutionRequest();
        submitReq.setWhatWasDone("Replaced worn HDMI cable with High-Speed 4K HDMI 2.1 cable.");
        submitReq.setFindings("Physical pin bent on legacy HDMI cord.");
        submitReq.setResolutionMessage("Monitor tested for 30 minutes with zero flicker.");

        mockMvc.perform(post("/api/v1/cases/" + caseId + "/resolution")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.requesterDecision").value("PENDING"))
                .andExpect(jsonPath("$.data.whatWasDone").value("Replaced worn HDMI cable with High-Speed 4K HDMI 2.1 cable."));

        // 2. Fetch resolution
        mockMvc.perform(get("/api/v1/cases/" + caseId + "/resolution")
                        .header("Authorization", "Bearer " + requesterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.findings").value("Physical pin bent on legacy HDMI cord."));

        // 3. Requester confirms resolution -> case closed
        ConfirmResolutionRequest confirmReq = new ConfirmResolutionRequest();
        confirmReq.setFeedback("Everything is working great now. Thanks!");

        mockMvc.perform(put("/api/v1/cases/" + caseId + "/resolution/confirm")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.requesterDecision").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.feedback").value("Everything is working great now. Thanks!"));
    }

    @Test
    @DisplayName("US-27: Requester Rejects Resolution -> Case Reopened")
    void resolution_reject() throws Exception {
        // Submit resolution
        SubmitResolutionRequest submitReq = new SubmitResolutionRequest();
        submitReq.setWhatWasDone("Updated display driver.");
        submitReq.setFindings("Old driver had bug with refresh rates.");
        submitReq.setResolutionMessage("Driver updated to v2.4.");

        mockMvc.perform(post("/api/v1/cases/" + caseId + "/resolution")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitReq)))
                .andExpect(status().isCreated());

        // Reject resolution
        RejectResolutionRequest rejectReq = new RejectResolutionRequest();
        rejectReq.setRejectionReason("The screen started flickering again after reboot.");

        mockMvc.perform(put("/api/v1/cases/" + caseId + "/resolution/reject")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.requesterDecision").value("REJECTED"))
                .andExpect(jsonPath("$.data.feedback").value("The screen started flickering again after reboot."));
    }

}
