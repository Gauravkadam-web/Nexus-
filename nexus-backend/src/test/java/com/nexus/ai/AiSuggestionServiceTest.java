package com.nexus.ai;

import com.nexus.ai.dto.SuggestionDecisionRequest;
import com.nexus.ai.entity.*;
import com.nexus.ai.repository.AiSuggestionRepository;
import com.nexus.ai.service.AiSuggestionService;
import com.nexus.casemanagement.entity.Case;
import com.nexus.common.exception.BadRequestException;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AiSuggestionService} — verifies the human-in-the-loop decision flow (US-14).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AiSuggestionService — Unit Tests")
class AiSuggestionServiceTest {

    @Mock AiSuggestionRepository aiSuggestionRepository;
    @Mock UserRepository userRepository;

    @InjectMocks AiSuggestionService service;

    private UUID suggestionId;
    private UUID operatorId;
    private AiSuggestion suggestion;
    private User operator;

    @BeforeEach
    void setUp() {
        suggestionId = UUID.randomUUID();
        operatorId = UUID.randomUUID();

        Case testCase = new Case();
        suggestion = new AiSuggestion(testCase, SuggestionType.PRIORITY, "{\"value\":\"HIGH\",\"confidence\":0.82}");

        operator = new User();
        operator.setName("Operator One");
    }

    @Test
    @DisplayName("decideSuggestion — ACCEPT sets status and records decided_by")
    void decideSuggestion_accept_updatesStatus() {
        when(aiSuggestionRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));
        when(userRepository.findById(operatorId)).thenReturn(Optional.of(operator));
        when(aiSuggestionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SuggestionDecisionRequest req = new SuggestionDecisionRequest();
        req.setStatus(SuggestionStatus.ACCEPTED);

        AiSuggestion result = service.decideSuggestion(suggestionId, req, operatorId);

        assertThat(result.getStatus()).isEqualTo(SuggestionStatus.ACCEPTED);
        assertThat(result.getDecidedBy()).isEqualTo(operator);
        assertThat(result.getDecidedAt()).isNotNull();
        assertThat(result.getModifiedValue()).isNull();
    }

    @Test
    @DisplayName("decideSuggestion — MODIFY records modifiedValue")
    void decideSuggestion_modify_recordsModifiedValue() {
        when(aiSuggestionRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));
        when(userRepository.findById(operatorId)).thenReturn(Optional.of(operator));
        when(aiSuggestionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SuggestionDecisionRequest req = new SuggestionDecisionRequest();
        req.setStatus(SuggestionStatus.MODIFIED);
        req.setModifiedValue("{\"value\":\"MEDIUM\"}");
        req.setOverrideReason("Requester described a minor issue");

        AiSuggestion result = service.decideSuggestion(suggestionId, req, operatorId);

        assertThat(result.getStatus()).isEqualTo(SuggestionStatus.MODIFIED);
        assertThat(result.getModifiedValue()).isEqualTo("{\"value\":\"MEDIUM\"}");
        assertThat(result.getOverrideReason()).isEqualTo("Requester described a minor issue");
    }

    @Test
    @DisplayName("decideSuggestion — REJECT sets status with optional reason")
    void decideSuggestion_reject_setsRejected() {
        when(aiSuggestionRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));
        when(userRepository.findById(operatorId)).thenReturn(Optional.of(operator));
        when(aiSuggestionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SuggestionDecisionRequest req = new SuggestionDecisionRequest();
        req.setStatus(SuggestionStatus.REJECTED);
        req.setOverrideReason("Priority is already correct per SLA agreement");

        AiSuggestion result = service.decideSuggestion(suggestionId, req, operatorId);

        assertThat(result.getStatus()).isEqualTo(SuggestionStatus.REJECTED);
        assertThat(result.getOverrideReason()).contains("SLA agreement");
    }

    @Test
    @DisplayName("decideSuggestion — throws BadRequestException if already decided")
    void decideSuggestion_alreadyDecided_throwsBadRequest() {
        suggestion.setStatus(SuggestionStatus.ACCEPTED);
        when(aiSuggestionRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));

        SuggestionDecisionRequest req = new SuggestionDecisionRequest();
        req.setStatus(SuggestionStatus.REJECTED);

        assertThatThrownBy(() -> service.decideSuggestion(suggestionId, req, operatorId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already been decided");

        verify(aiSuggestionRepository, never()).save(any());
    }

    @Test
    @DisplayName("decideSuggestion — MODIFY without modifiedValue throws BadRequestException")
    void decideSuggestion_modifyWithoutValue_throwsBadRequest() {
        when(aiSuggestionRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));
        when(userRepository.findById(operatorId)).thenReturn(Optional.of(operator));

        SuggestionDecisionRequest req = new SuggestionDecisionRequest();
        req.setStatus(SuggestionStatus.MODIFIED);
        // No modifiedValue

        assertThatThrownBy(() -> service.decideSuggestion(suggestionId, req, operatorId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("modifiedValue");
    }
}
