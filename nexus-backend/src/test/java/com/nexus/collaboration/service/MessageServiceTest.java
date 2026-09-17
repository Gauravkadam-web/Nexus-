package com.nexus.collaboration.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.casemanagement.statemachine.CaseLifecycleService;
import com.nexus.collaboration.dto.MessageRequest;
import com.nexus.collaboration.dto.MessageResponse;
import com.nexus.collaboration.entity.CaseMessage;
import com.nexus.collaboration.entity.MessageType;
import com.nexus.collaboration.repository.CaseMessageRepository;
import com.nexus.user.entity.Role;
import com.nexus.user.entity.RoleType;
import com.nexus.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private CaseMessageRepository messageRepository;

    @Mock
    private CaseRepository caseRepository;

    private CaseLifecycleService caseLifecycleService;

    private MessageService messageService;

    private User operator;
    private User requester;
    private Case testCase;

    @BeforeEach
    void setUp() {
        caseLifecycleService = new CaseLifecycleService();
        messageService = new MessageService(messageRepository, caseRepository, caseLifecycleService);
        operator = new User();
        operator.setId(UUID.randomUUID());
        operator.setName("Test Operator");
        operator.setEmail("operator@nexus.com");
        operator.setRoles(Set.of(new Role(RoleType.OPERATOR)));

        requester = new User();
        requester.setId(UUID.randomUUID());
        requester.setName("Test Requester");
        requester.setEmail("requester@nexus.com");
        requester.setRoles(Set.of(new Role(RoleType.REQUESTER)));

        testCase = new Case();
        testCase.setId(UUID.randomUUID());
        testCase.setCaseNumber("NEX-20260917-0001");
        testCase.setStatus(CaseStatus.INVESTIGATING);
    }

    @Test
    @DisplayName("US-6: Sending QUESTION message automatically transitions Case to WAITING_FOR_INFO")
    void testQuestionMessageTransitionsToWaitingForInfo() {
        when(caseRepository.findById(testCase.getId())).thenReturn(Optional.of(testCase));
        when(messageRepository.save(any(CaseMessage.class))).thenAnswer(i -> {
            CaseMessage m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        MessageRequest request = new MessageRequest(MessageType.QUESTION, "Could you provide your error screenshot?", true);
        MessageResponse response = messageService.sendMessage(testCase.getId(), request, operator);

        assertThat(response).isNotNull();
        assertThat(response.getMessageType()).isEqualTo(MessageType.QUESTION);
        assertThat(testCase.getStatus()).isEqualTo(CaseStatus.WAITING_FOR_INFO);
        verify(caseRepository).save(testCase);
    }

    @Test
    @DisplayName("US-7: Requester answering moves Case back from WAITING_FOR_INFO to INVESTIGATING")
    void testAnswerMessageTransitionsBackToInvestigating() {
        testCase.setStatus(CaseStatus.WAITING_FOR_INFO);
        when(caseRepository.findById(testCase.getId())).thenReturn(Optional.of(testCase));
        when(messageRepository.save(any(CaseMessage.class))).thenAnswer(i -> {
            CaseMessage m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        MessageRequest request = new MessageRequest(MessageType.ANSWER, "Here is the error details: 403 Forbidden", true);
        MessageResponse response = messageService.sendMessage(testCase.getId(), request, requester);

        assertThat(response).isNotNull();
        assertThat(response.getMessageType()).isEqualTo(MessageType.ANSWER);
        assertThat(testCase.getStatus()).isEqualTo(CaseStatus.INVESTIGATING);
        verify(caseRepository).save(testCase);
    }

    @Test
    @DisplayName("US-8: Requester only sees messages marked visible_to_requester=true")
    void testRequesterVisibilityFilter() {
        when(caseRepository.existsById(testCase.getId())).thenReturn(true);

        CaseMessage visibleMsg = new CaseMessage(testCase, operator, MessageType.UPDATE, "Public update", true);
        visibleMsg.setId(UUID.randomUUID());

        when(messageRepository.findByCaseEntityIdAndVisibleToRequesterTrueOrderByCreatedAtAsc(testCase.getId()))
                .thenReturn(List.of(visibleMsg));

        List<MessageResponse> messages = messageService.getMessages(testCase.getId(), requester);

        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("Public update");
        verify(messageRepository).findByCaseEntityIdAndVisibleToRequesterTrueOrderByCreatedAtAsc(testCase.getId());
        verify(messageRepository, never()).findByCaseEntityIdOrderByCreatedAtAsc(any());
    }

    @Test
    @DisplayName("US-8: Operator sees all messages including invisible notes")
    void testOperatorSeesAllMessages() {
        when(caseRepository.existsById(testCase.getId())).thenReturn(true);

        CaseMessage msg1 = new CaseMessage(testCase, operator, MessageType.UPDATE, "Public update", true);
        msg1.setId(UUID.randomUUID());
        CaseMessage msg2 = new CaseMessage(testCase, operator, MessageType.FOLLOW_UP, "Operator internal follow up", false);
        msg2.setId(UUID.randomUUID());

        when(messageRepository.findByCaseEntityIdOrderByCreatedAtAsc(testCase.getId()))
                .thenReturn(List.of(msg1, msg2));

        List<MessageResponse> messages = messageService.getMessages(testCase.getId(), operator);

        assertThat(messages).hasSize(2);
        verify(messageRepository).findByCaseEntityIdOrderByCreatedAtAsc(testCase.getId());
    }
}
