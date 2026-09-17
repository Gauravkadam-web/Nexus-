package com.nexus.collaboration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.auth.dto.AuthResponse;
import com.nexus.auth.dto.RegisterRequest;
import com.nexus.auth.service.AuthService;
import com.nexus.casemanagement.dto.CreateCaseRequest;
import com.nexus.casemanagement.dto.UpdateCaseStatusRequest;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import com.nexus.collaboration.dto.CreateTaskRequest;
import com.nexus.collaboration.dto.InternalNoteRequest;
import com.nexus.collaboration.dto.InvestigationRequest;
import com.nexus.collaboration.dto.MessageRequest;
import com.nexus.collaboration.dto.UpdateTaskStatusRequest;
import com.nexus.collaboration.entity.MessageType;
import com.nexus.collaboration.entity.TaskPriority;
import com.nexus.collaboration.entity.TaskStatus;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.user.entity.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CollaborationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TeamRepository teamRepository;

    private String requesterToken;
    private String operatorToken;
    private String teamLeadToken;
    private UUID categoryId;
    private UUID teamId;
    private UUID caseId;

    @BeforeEach
    void setUp() throws Exception {
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        Organization org = new Organization();
        org.setName("Collab Test Org " + randomSuffix);
        org = organizationRepository.save(org);

        Category category = new Category(org, "Hardware Support " + randomSuffix, null, null);
        category = categoryRepository.save(category);
        categoryId = category.getId();

        Team team = new Team(org, "Support Desk " + randomSuffix, null);
        team = teamRepository.save(team);
        teamId = team.getId();

        RegisterRequest reqRegister = new RegisterRequest();
        reqRegister.setOrganizationName(org.getName());
        reqRegister.setName("Collab Requester");
        reqRegister.setEmail("req_" + randomSuffix + "@collab.com");
        reqRegister.setPassword("Password@123");
        reqRegister.setRole(RoleType.REQUESTER);
        AuthResponse reqAuth = authService.register(reqRegister);
        requesterToken = reqAuth.getAccessToken();

        RegisterRequest opRegister = new RegisterRequest();
        opRegister.setOrganizationName(org.getName());
        opRegister.setName("Collab Operator");
        opRegister.setEmail("op_" + randomSuffix + "@collab.com");
        opRegister.setPassword("Password@123");
        opRegister.setRole(RoleType.OPERATOR);
        AuthResponse opAuth = authService.register(opRegister);
        operatorToken = opAuth.getAccessToken();

        RegisterRequest leadRegister = new RegisterRequest();
        leadRegister.setOrganizationName(org.getName());
        leadRegister.setName("Collab TeamLead");
        leadRegister.setEmail("lead_" + randomSuffix + "@collab.com");
        leadRegister.setPassword("Password@123");
        leadRegister.setRole(RoleType.TEAM_LEAD);
        AuthResponse leadAuth = authService.register(leadRegister);
        teamLeadToken = leadAuth.getAccessToken();

        // Create initial case
        CreateCaseRequest createRequest = new CreateCaseRequest();
        createRequest.setTitle("Laptop display flickering");
        createRequest.setDescription("Screen goes black intermittently.");
        createRequest.setCategoryId(categoryId);
        createRequest.setSeverity(Severity.MEDIUM);
        createRequest.setPriority(Priority.HIGH);

        MvcResult result = mockMvc.perform(post("/api/v1/cases")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        caseId = UUID.fromString(objectMapper.readTree(responseJson).get("data").get("id").asText());

        // Move to UNDERSTOOD -> ASSIGNED -> INVESTIGATING
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
    @DisplayName("US-6 & US-7: End-to-end Info Request and Response Cycle with State Transitions")
    void testMissingInfoRequestAndResponseCycle() throws Exception {
        // Step 1: Operator asks for missing info (US-6)
        MessageRequest question = new MessageRequest(MessageType.QUESTION, "Does this occur on external monitor?", true);
        mockMvc.perform(post("/api/v1/cases/" + caseId + "/messages")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.messageType", is("QUESTION")));

        // Verify case is now WAITING_FOR_INFO
        mockMvc.perform(get("/api/v1/cases/" + caseId)
                        .header("Authorization", "Bearer " + requesterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("WAITING_FOR_INFO")));

        // Step 2: Requester responds with ANSWER (US-7)
        MessageRequest answer = new MessageRequest(MessageType.ANSWER, "No, external HDMI monitor works fine.", true);
        mockMvc.perform(post("/api/v1/cases/" + caseId + "/messages")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.messageType", is("ANSWER")));

        // Verify case automatically transitioned back to INVESTIGATING
        mockMvc.perform(get("/api/v1/cases/" + caseId)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("INVESTIGATING")));
    }

    @Test
    @DisplayName("US-8: Internal Notes CRUD and RBAC protection")
    void testInternalNotesRbac() throws Exception {
        // Operator adds note
        InternalNoteRequest noteReq = new InternalNoteRequest("Suspected GPU ribbon cable loose connection.");
        mockMvc.perform(post("/api/v1/cases/" + caseId + "/notes")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.content", is("Suspected GPU ribbon cable loose connection.")));

        // Operator retrieves notes
        mockMvc.perform(get("/api/v1/cases/" + caseId + "/notes")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        // Requester gets 403 Forbidden
        mockMvc.perform(post("/api/v1/cases/" + caseId + "/notes")
                        .header("Authorization", "Bearer " + requesterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteReq)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("US-9: Task creation, listing and status progression")
    void testTaskManagementLifecycle() throws Exception {
        CreateTaskRequest taskReq = new CreateTaskRequest("Inspect hardware cable", "Open casing and reseat EDP cable", null, TaskPriority.HIGH, null);

        MvcResult createResult = mockMvc.perform(post("/api/v1/cases/" + caseId + "/tasks")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title", is("Inspect hardware cable")))
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andReturn();

        UUID taskId = UUID.fromString(objectMapper.readTree(createResult.getResponse().getContentAsString()).get("data").get("id").asText());

        // Update task status to COMPLETED
        mockMvc.perform(patch("/api/v1/tasks/" + taskId + "/status")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateTaskStatusRequest(TaskStatus.COMPLETED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("COMPLETED")))
                .andExpect(jsonPath("$.data.completedAt", notNullValue()));
    }

    @Test
    @DisplayName("US-9: Investigation logs creation and retrieval")
    void testInvestigationLogging() throws Exception {
        InvestigationRequest invReq = new InvestigationRequest(
                "Internal display drops frames at 144Hz",
                "Swapped EDP cable and tested bench refresh rate",
                "Damaged solder on display harness",
                "ATTACHMENT-REF-101",
                "Order replacement harness from Dell OEM"
        );

        mockMvc.perform(post("/api/v1/cases/" + caseId + "/investigations")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.finding", is("Damaged solder on display harness")));

        mockMvc.perform(get("/api/v1/cases/" + caseId + "/investigations")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @DisplayName("US-10: Team Lead workload monitoring endpoint")
    void testTeamWorkloadMonitoring() throws Exception {
        mockMvc.perform(get("/api/v1/collaboration/workload/team")
                        .header("Authorization", "Bearer " + teamLeadToken)
                        .param("teamId", teamId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teamId", is(teamId.toString())))
                .andExpect(jsonPath("$.data.totalOpenCases", notNullValue()));
    }

    @Test
    @DisplayName("Attachments upload and listing")
    void testAttachmentUploadAndList() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "screenshot.png",
                "image/png",
                "fake image byte content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/cases/" + caseId + "/attachments")
                        .file(file)
                        .header("Authorization", "Bearer " + requesterToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.fileName", is("screenshot.png")));

        mockMvc.perform(get("/api/v1/cases/" + caseId + "/attachments")
                        .header("Authorization", "Bearer " + requesterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}
