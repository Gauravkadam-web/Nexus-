package com.nexus.ai.service;

import com.nexus.ai.dto.DuplicateSuggestionResponse;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CaseDuplicateDetectionService — Unit Tests")
class CaseDuplicateDetectionServiceTest {

    @Mock CaseRepository caseRepository;

    @InjectMocks CaseDuplicateDetectionService service;

    private Organization org;
    private Category category;
    private Case targetCase;
    private Case duplicateCase;
    private Case unrelatedCase;
    private UUID targetId;

    @BeforeEach
    void setUp() {
        org = new Organization("Test Org");
        org.setId(UUID.randomUUID());
        category = new Category(org, "Network Issues", null, null);
        category.setId(UUID.randomUUID());
        targetId = UUID.randomUUID();

        targetCase = new Case();
        targetCase.setId(targetId);
        targetCase.setCaseNumber("NX-001");
        targetCase.setTitle("VPN connection failing with timeout");
        targetCase.setDescription("Users in Pune office cannot connect to Cisco VPN");
        targetCase.setCategory(category);
        targetCase.setStatus(CaseStatus.REPORTED);

        duplicateCase = new Case();
        duplicateCase.setId(UUID.randomUUID());
        duplicateCase.setCaseNumber("NX-002");
        duplicateCase.setTitle("Cisco VPN timeout in Pune office");
        duplicateCase.setDescription("Pune office users unable to connect to VPN");
        duplicateCase.setCategory(category);
        duplicateCase.setStatus(CaseStatus.INVESTIGATING);

        unrelatedCase = new Case();
        unrelatedCase.setId(UUID.randomUUID());
        unrelatedCase.setCaseNumber("NX-003");
        unrelatedCase.setTitle("Printer out of toner");
        unrelatedCase.setDescription("Floor 3 printer needs black toner cartridge");
        unrelatedCase.setCategory(category);
        unrelatedCase.setStatus(CaseStatus.REPORTED);
    }

    @Test
    @DisplayName("findPotentialDuplicates — finds matching candidate with high similarity")
    void findPotentialDuplicates_findsMatch() {
        when(caseRepository.findById(targetId)).thenReturn(Optional.of(targetCase));
        when(caseRepository.findAll()).thenReturn(List.of(targetCase, duplicateCase, unrelatedCase));

        List<DuplicateSuggestionResponse> result = service.findPotentialDuplicates(targetId);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCandidateCaseId()).isEqualTo(duplicateCase.getId());
        assertThat(result.get(0).getSimilarityScore().doubleValue()).isGreaterThan(0.30);
        assertThat(result.get(0).getMatchReason()).contains("Network Issues");
    }

    @Test
    @DisplayName("findPotentialDuplicates — returns empty list when no similar cases exist")
    void findPotentialDuplicates_noMatches_returnsEmpty() {
        when(caseRepository.findById(targetId)).thenReturn(Optional.of(targetCase));
        when(caseRepository.findAll()).thenReturn(List.of(targetCase, unrelatedCase));

        List<DuplicateSuggestionResponse> result = service.findPotentialDuplicates(targetId);

        assertThat(result).isEmpty();
    }
}
