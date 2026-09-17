package com.nexus.ai.service;

import com.nexus.ai.dto.*;
import com.nexus.ai.provider.AiProviderPort;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.entity.CaseMessage;
import com.nexus.collaboration.entity.CaseTask;
import com.nexus.collaboration.entity.Investigation;
import com.nexus.collaboration.repository.CaseMessageRepository;
import com.nexus.collaboration.repository.CaseTaskRepository;
import com.nexus.collaboration.repository.InvestigationRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service orchestrating AI Operator Copilot (US-29) and AI Communication Drafting (US-30).
 * Answers case-scoped operator questions and prepares context-aware communication drafts.
 */
@Service
public class AiCopilotService {

    private static final Logger log = LoggerFactory.getLogger(AiCopilotService.class);

    private final CaseRepository caseRepository;
    private final InvestigationRepository investigationRepository;
    private final CaseTaskRepository caseTaskRepository;
    private final CaseMessageRepository caseMessageRepository;
    private final AiAnalysisService aiAnalysisService;
    private final AiProviderPort aiProvider;

    public AiCopilotService(CaseRepository caseRepository,
                            InvestigationRepository investigationRepository,
                            CaseTaskRepository caseTaskRepository,
                            CaseMessageRepository caseMessageRepository,
                            AiAnalysisService aiAnalysisService,
                            AiProviderPort aiProvider) {
        this.caseRepository = caseRepository;
        this.investigationRepository = investigationRepository;
        this.caseTaskRepository = caseTaskRepository;
        this.caseMessageRepository = caseMessageRepository;
        this.aiAnalysisService = aiAnalysisService;
        this.aiProvider = aiProvider;
    }

    /**
     * Answers a case-scoped operator query using full case investigation context (US-29).
     *
     * @param caseId  the UUID of the case
     * @param request the copilot query
     * @return populated {@link AiCopilotResponse}
     */
    @Transactional(readOnly = true)
    public AiCopilotResponse askCopilot(UUID caseId, AiCopilotRequest request) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

        String fullTimeline = buildFullCaseContext(caseId);
        CaseContext context = aiAnalysisService.buildContext(caseEntity, fullTimeline);

        String answer = aiProvider.askCopilot(context, request.getQuestion());

        List<String> sourcesUsed = new ArrayList<>();
        sourcesUsed.add("case_metadata");
        sourcesUsed.add("case_description");
        if (!fullTimeline.isBlank()) {
            sourcesUsed.add("investigation_timeline");
            sourcesUsed.add("case_messages");
        }

        log.info("[AI Copilot] Answered query for case {}: '{}'", caseEntity.getCaseNumber(), request.getQuestion());

        return new AiCopilotResponse(
                request.getQuestion(),
                answer,
                new BigDecimal("0.88"),
                sourcesUsed
        );
    }

    /**
     * Drafts professional case communication based on case context and operator intent (US-30).
     *
     * @param caseId  the UUID of the case
     * @param request audience, intent, and instructions
     * @return populated {@link AiDraftCommunicationResponse}
     */
    @Transactional(readOnly = true)
    public AiDraftCommunicationResponse draftCommunication(UUID caseId, AiDraftCommunicationRequest request) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

        String fullTimeline = buildFullCaseContext(caseId);
        CaseContext context = aiAnalysisService.buildContext(caseEntity, fullTimeline);

        String draftBody = aiProvider.draftCommunication(
                context,
                request.getAudience(),
                request.getIntent(),
                request.getInstructions()
        );

        String subject = String.format("Update regarding Case #%s: %s", caseEntity.getCaseNumber(), caseEntity.getTitle());
        String tone = "REQUESTER".equalsIgnoreCase(request.getAudience()) 
                ? "Professional & Empathetic" 
                : "Technical & Objective";

        log.info("[AI Draft Communication] Drafted message for case {}, audience: {}, intent: {}",
                caseEntity.getCaseNumber(), request.getAudience(), request.getIntent());

        return new AiDraftCommunicationResponse(
                subject,
                draftBody,
                tone,
                request.getAudience()
        );
    }

    // ---- Helper: Comprehensive Context Gathering ----

    private String buildFullCaseContext(UUID caseId) {
        StringBuilder sb = new StringBuilder();

        // 1. Investigation records
        List<Investigation> investigations = investigationRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId);
        if (!investigations.isEmpty()) {
            sb.append("--- Investigation Records ---\n");
            for (Investigation inv : investigations) {
                sb.append(String.format("[%s] Observation: %s | Action: %s | Finding: %s\n",
                        inv.getCreatedAt(), inv.getObservation(), inv.getActionTaken(), inv.getFinding()));
            }
        }


        // 2. Tasks
        List<CaseTask> tasks = caseTaskRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId);
        if (!tasks.isEmpty()) {
            sb.append("--- Tasks ---\n");
            for (CaseTask task : tasks) {
                sb.append(String.format("[%s] Task: %s (Status: %s)\n", task.getCreatedAt(), task.getTitle(), task.getStatus()));
            }
        }

        // 3. Case Messages
        List<CaseMessage> messages = caseMessageRepository.findByCaseEntityIdOrderByCreatedAtAsc(caseId);
        if (!messages.isEmpty()) {
            sb.append("--- Case Messages ---\n");
            for (CaseMessage msg : messages) {
                sb.append(String.format("[%s] %s (%s): %s\n", 
                        msg.getCreatedAt(), msg.getMessageType(), 
                        Boolean.TRUE.equals(msg.getVisibleToRequester()) ? "Public" : "Internal", 
                        msg.getContent()));
            }
        }

        return sb.toString();
    }
}
