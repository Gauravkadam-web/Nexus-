package com.nexus.ai.service;

import com.nexus.ai.dto.*;
import com.nexus.ai.provider.AiProviderPort;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.entity.Investigation;
import com.nexus.collaboration.repository.CaseMessageRepository;
import com.nexus.collaboration.repository.CaseTaskRepository;
import com.nexus.collaboration.repository.InvestigationRepository;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiCopilotService — Unit Tests")
class AiCopilotServiceTest {

    @Mock private CaseRepository caseRepository;
    @Mock private InvestigationRepository investigationRepository;
    @Mock private CaseTaskRepository caseTaskRepository;
    @Mock private CaseMessageRepository caseMessageRepository;
    @Mock private AiProviderPort aiProvider;

    private AiAnalysisService aiAnalysisService;
    private AiCopilotService copilotService;

    private Organization org;
    private Category cat;
    private User requester;
    private Case testCase;

    @BeforeEach
    void setUp() {
        aiAnalysisService = new AiAnalysisService(null, null, null, null, null, null);
        copilotService = new AiCopilotService(
                caseRepository,
                investigationRepository,
                caseTaskRepository,
                caseMessageRepository,
                aiAnalysisService,
                aiProvider
        );

        org = new Organization("Test Org");
        cat = new Category(org, "Network", null, null);
        requester = new User(org, "Alice", "alice@nexus.com", "pass");

        testCase = new Case();
        testCase.setCaseNumber("CAS-2026-0001");
        testCase.setTitle("VPN issue");
        testCase.setDescription("Cannot connect to VPN");
        testCase.setCategory(cat);
        testCase.setPriority(Priority.HIGH);
        testCase.setSeverity(Severity.MEDIUM);
        testCase.setRequester(requester);
    }

    @Test
    @DisplayName("US-29: askCopilot gathers case timeline and returns copilot response")
    void askCopilot_success() {
        UUID caseId = testCase.getId();
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        when(investigationRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)).thenReturn(
                List.of(new Investigation(testCase, requester, "Checked VPN Gateway", "Pinged endpoint", "Gateway responding normally", null, null))
        );
        when(caseTaskRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)).thenReturn(List.of());
        when(caseMessageRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)).thenReturn(List.of());

        when(aiProvider.askCopilot(any(CaseContext.class), eq("What is blocking resolution?")))
                .thenReturn("Awaiting firewall configuration changes from network admin.");

        AiCopilotRequest req = new AiCopilotRequest("What is blocking resolution?");
        AiCopilotResponse response = copilotService.askCopilot(caseId, req);

        assertThat(response).isNotNull();
        assertThat(response.getQuestion()).isEqualTo("What is blocking resolution?");
        assertThat(response.getAnswer()).isEqualTo("Awaiting firewall configuration changes from network admin.");
        assertThat(response.getSourcesUsed()).contains("case_metadata", "investigation_timeline");
    }

    @Test
    @DisplayName("US-30: draftCommunication drafts context-aware message")
    void draftCommunication_success() {
        UUID caseId = testCase.getId();
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        when(investigationRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)).thenReturn(List.of());
        when(caseTaskRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)).thenReturn(List.of());
        when(caseMessageRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId)).thenReturn(List.of());

        when(aiProvider.draftCommunication(any(CaseContext.class), eq("REQUESTER"), eq("STATUS_UPDATE"), eq("Mention maintenance window")))
                .thenReturn("Dear Requester, we are working on your case during the scheduled maintenance window.");

        AiDraftCommunicationRequest req = new AiDraftCommunicationRequest("REQUESTER", "STATUS_UPDATE", "Mention maintenance window");
        AiDraftCommunicationResponse response = copilotService.draftCommunication(caseId, req);

        assertThat(response).isNotNull();
        assertThat(response.getRecipientRole()).isEqualTo("REQUESTER");
        assertThat(response.getTone()).isEqualTo("Professional & Empathetic");
        assertThat(response.getDraftSubject()).contains("CAS-2026-0001");
        assertThat(response.getDraftBody()).contains("Dear Requester");
    }
}
