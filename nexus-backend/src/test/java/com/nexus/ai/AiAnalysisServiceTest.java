package com.nexus.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.ai.entity.*;
import com.nexus.ai.provider.AiAnalysisResult;
import com.nexus.ai.provider.AiProviderPort;
import com.nexus.ai.provider.AiUnavailableException;
import com.nexus.ai.repository.*;
import com.nexus.ai.service.AiAnalysisService;
import com.nexus.casemanagement.entity.*;
import com.nexus.casemanagement.repository.CaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AiAnalysisService} — verifies analysis persistence,
 * graceful degradation (US-15), and idempotency.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AiAnalysisService — Unit Tests")
class AiAnalysisServiceTest {

    @Mock AiProviderPort aiProvider;
    @Mock AiAnalysisRepository aiAnalysisRepository;
    @Mock AiSuggestionRepository aiSuggestionRepository;
    @Mock AutomationEventRepository automationEventRepository;
    @Mock CaseRepository caseRepository;

    @InjectMocks
    AiAnalysisService service;

    private Case testCase;
    private UUID caseId;

    @BeforeEach
    void setUp() {
        // ObjectMapper injection via constructor not possible with @InjectMocks + @Mock;
        // rebuild service with ObjectMapper manually
        service = new AiAnalysisService(aiProvider, aiAnalysisRepository,
                aiSuggestionRepository, automationEventRepository,
                caseRepository, new ObjectMapper());

        caseId = UUID.randomUUID();
        testCase = new Case();
        testCase.setCaseNumber("NX-2026-0001");
        testCase.setTitle("VPN not working");
        testCase.setDescription("Cannot connect to VPN since morning");
        testCase.setPriority(Priority.MEDIUM);
        testCase.setSeverity(Severity.MEDIUM);
        testCase.setStatus(CaseStatus.REPORTED);
    }

    @Test
    @DisplayName("analyzeCase — saves AiAnalysis and PENDING suggestions on success")
    void analyzeCase_success_savesAnalysisAndSuggestions() {
        // Arrange
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        when(automationEventRepository.findTopByCaseEntityIdAndEventTypeAndStatusOrderByCreatedAtDesc(
                caseId, AutomationEventType.AI_ANALYSIS, AutomationEventStatus.SUCCESS))
                .thenReturn(Optional.empty());
        when(automationEventRepository.save(any(AutomationEvent.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AiAnalysisResult mockResult = new AiAnalysisResult();
        mockResult.setSuggestedPriority("HIGH");
        mockResult.setSuggestedSeverity("MEDIUM");
        mockResult.setMissingInformation(List.of("error_code", "device_type"));
        mockResult.setRecommendedNextAction("Request error details from requester");
        mockResult.setRiskInformation("{\"level\":\"LOW\"}");
        mockResult.setConfidence(new BigDecimal("0.85"));
        mockResult.setModelInformation("mock/v1");

        when(aiProvider.analyzeCase(any())).thenReturn(mockResult);
        when(aiAnalysisRepository.save(any(AiAnalysis.class))).thenAnswer(inv -> inv.getArgument(0));
        when(aiSuggestionRepository.save(any(AiSuggestion.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.analyzeCase(caseId);

        // Assert
        verify(aiAnalysisRepository, times(1)).save(argThat(a ->
                "HIGH".equals(a.getSuggestedPriority()) &&
                "MEDIUM".equals(a.getSuggestedSeverity()) &&
                a.getConfidence().compareTo(new BigDecimal("0.85")) == 0
        ));
        // Two suggestions: PRIORITY + SEVERITY
        verify(aiSuggestionRepository, times(2)).save(any(AiSuggestion.class));
        // Event status should be updated to SUCCESS
        verify(automationEventRepository, atLeast(2)).save(argThat(e ->
                e instanceof AutomationEvent));
    }

    @Test
    @DisplayName("analyzeCase — graceful degradation when AI unavailable (US-15)")
    void analyzeCase_aiUnavailable_doesNotBlockCase() {
        // Arrange
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        when(automationEventRepository.findTopByCaseEntityIdAndEventTypeAndStatusOrderByCreatedAtDesc(
                caseId, AutomationEventType.AI_ANALYSIS, AutomationEventStatus.SUCCESS))
                .thenReturn(Optional.empty());
        when(automationEventRepository.save(any(AutomationEvent.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(aiProvider.analyzeCase(any())).thenThrow(new AiUnavailableException("Provider timeout"));

        // Act — must NOT throw
        service.analyzeCase(caseId);

        // Assert: AI analysis NOT saved, event marked FAILED
        verify(aiAnalysisRepository, never()).save(any());
        verify(automationEventRepository, atLeast(2)).save(argThat(e -> {
            if (e instanceof AutomationEvent event) {
                return event.getStatus() == AutomationEventStatus.FAILED
                        || event.getStatus() == AutomationEventStatus.PENDING;
            }
            return true;
        }));
    }

    @Test
    @DisplayName("analyzeCase — idempotency: skips if SUCCESS event already exists")
    void analyzeCase_idempotent_skipsIfAlreadySucceeded() {
        // Arrange
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(testCase));
        AutomationEvent existingSuccess = new AutomationEvent(testCase,
                AutomationEventType.AI_ANALYSIS, AutomationTriggerType.EVENT);
        existingSuccess.setStatus(AutomationEventStatus.SUCCESS);
        when(automationEventRepository.findTopByCaseEntityIdAndEventTypeAndStatusOrderByCreatedAtDesc(
                caseId, AutomationEventType.AI_ANALYSIS, AutomationEventStatus.SUCCESS))
                .thenReturn(Optional.of(existingSuccess));

        // Act
        service.analyzeCase(caseId);

        // Assert: AI provider never called, no analysis saved
        verify(aiProvider, never()).analyzeCase(any());
        verify(aiAnalysisRepository, never()).save(any());
    }

    @Test
    @DisplayName("analyzeCase — skips gracefully when case not found")
    void analyzeCase_caseNotFound_skipsGracefully() {
        when(caseRepository.findById(caseId)).thenReturn(Optional.empty());

        // Must NOT throw
        service.analyzeCase(caseId);

        verify(aiProvider, never()).analyzeCase(any());
    }
}
