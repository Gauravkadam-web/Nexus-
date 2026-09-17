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
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.user.entity.RoleType;
import com.nexus.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final CaseMessageRepository messageRepository;
    private final CaseRepository caseRepository;
    private final CaseLifecycleService caseLifecycleService;

    public MessageService(CaseMessageRepository messageRepository,
                          CaseRepository caseRepository,
                          CaseLifecycleService caseLifecycleService) {
        this.messageRepository = messageRepository;
        this.caseRepository = caseRepository;
        this.caseLifecycleService = caseLifecycleService;
    }

    @Transactional
    public MessageResponse sendMessage(UUID caseId, MessageRequest request, User sender) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        // US-6: Operator/Lead requesting information triggers WAITING_FOR_INFO state
        if (request.getMessageType() == MessageType.QUESTION || request.getMessageType() == MessageType.EVIDENCE_REQUEST) {
            if (caseEntity.getStatus() == CaseStatus.INVESTIGATING || caseEntity.getStatus() == CaseStatus.ASSIGNED) {
                caseLifecycleService.validateTransition(caseEntity.getStatus(), CaseStatus.WAITING_FOR_INFO);
                caseEntity.setStatus(CaseStatus.WAITING_FOR_INFO);
                caseRepository.save(caseEntity);
            }
        }
        // US-7: Requester responding to info request moves case back to INVESTIGATING
        else if (request.getMessageType() == MessageType.ANSWER) {
            if (caseEntity.getStatus() == CaseStatus.WAITING_FOR_INFO) {
                caseLifecycleService.validateTransition(CaseStatus.WAITING_FOR_INFO, CaseStatus.INVESTIGATING);
                caseEntity.setStatus(CaseStatus.INVESTIGATING);
                caseRepository.save(caseEntity);
            }
        }

        CaseMessage message = new CaseMessage(
                caseEntity,
                sender,
                request.getMessageType(),
                request.getContent(),
                request.getVisibleToRequester()
        );

        CaseMessage saved = messageRepository.save(message);
        return MessageResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(UUID caseId, User currentUser) {
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case", "id", caseId);
        }

        boolean isStaff = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleType.OPERATOR
                        || r.getName() == RoleType.TEAM_LEAD
                        || r.getName() == RoleType.MANAGER
                        || r.getName() == RoleType.ADMIN);

        List<CaseMessage> messages;
        if (isStaff) {
            messages = messageRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId);
        } else {
            messages = messageRepository.findByCaseEntityIdAndVisibleToRequesterTrueOrderByCreatedAtAsc(caseId);
        }

        return messages.stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
