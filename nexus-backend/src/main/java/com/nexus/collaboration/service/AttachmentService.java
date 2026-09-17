package com.nexus.collaboration.service;

import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.collaboration.dto.AttachmentResponse;
import com.nexus.collaboration.entity.CaseAttachment;
import com.nexus.collaboration.repository.CaseAttachmentRepository;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttachmentService {

    private final CaseAttachmentRepository attachmentRepository;
    private final CaseRepository caseRepository;
    private final StorageService storageService;

    public AttachmentService(CaseAttachmentRepository attachmentRepository,
                             CaseRepository caseRepository,
                             StorageService storageService) {
        this.attachmentRepository = attachmentRepository;
        this.caseRepository = caseRepository;
        this.storageService = storageService;
    }

    @Transactional
    public AttachmentResponse uploadAttachment(UUID caseId, MultipartFile file, User user) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId));

        String storagePath = storageService.storeFile(file);
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String fileType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        Long fileSize = file.getSize();

        CaseAttachment attachment = new CaseAttachment(caseEntity, user, storagePath, fileName, fileType, fileSize);
        CaseAttachment saved = attachmentRepository.save(attachment);

        return AttachmentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachments(UUID caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new ResourceNotFoundException("Case", "id", caseId);
        }
        return attachmentRepository.findByCaseEntityIdOrderByCreatedAtDesc(caseId)
                .stream()
                .map(AttachmentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
