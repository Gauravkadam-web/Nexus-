package com.nexus.casemanagement.service;

import com.nexus.casemanagement.dto.CaseRelationResponse;
import com.nexus.casemanagement.dto.CreateCaseRelationRequest;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseRelation;
import com.nexus.casemanagement.entity.RelationType;
import com.nexus.casemanagement.repository.CaseRelationRepository;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CaseRelationService — Unit Tests")
class CaseRelationServiceTest {

    @Mock CaseRelationRepository caseRelationRepository;
    @Mock CaseRepository caseRepository;
    @Mock UserRepository userRepository;

    @InjectMocks CaseRelationService service;

    private Organization org;
    private Case primaryCase;
    private Case relatedCase;
    private User operator;
    private UUID primaryId;
    private UUID relatedId;
    private UUID operatorId;

    @BeforeEach
    void setUp() {
        org = new Organization("Test Org");
        org.setId(UUID.randomUUID());
        primaryId = UUID.randomUUID();
        relatedId = UUID.randomUUID();
        operatorId = UUID.randomUUID();

        Category cat = new Category(org, "IT Support", null, null);
        cat.setId(UUID.randomUUID());

        primaryCase = new Case();
        primaryCase.setId(primaryId);
        primaryCase.setCaseNumber("NX-2026-0001");
        primaryCase.setTitle("Primary VPN Outage");
        primaryCase.setCategory(cat);

        relatedCase = new Case();
        relatedCase.setId(relatedId);
        relatedCase.setCaseNumber("NX-2026-0002");
        relatedCase.setTitle("Duplicate VPN Outage");
        relatedCase.setCategory(cat);

        operator = new User();
        operator.setName("Operator John");
    }

    @Test
    @DisplayName("linkCases — successfully links cases as DUPLICATE")
    void linkCases_success() {
        when(caseRepository.findById(primaryId)).thenReturn(Optional.of(primaryCase));
        when(caseRepository.findById(relatedId)).thenReturn(Optional.of(relatedCase));
        when(caseRelationRepository.existsByCaseEntityIdAndRelatedCaseEntityIdAndRelationType(primaryId, relatedId, RelationType.DUPLICATE))
                .thenReturn(false);
        when(caseRelationRepository.existsByCaseEntityIdAndRelatedCaseEntityIdAndRelationType(relatedId, primaryId, RelationType.DUPLICATE))
                .thenReturn(false);
        when(userRepository.findById(operatorId)).thenReturn(Optional.of(operator));
        when(caseRelationRepository.save(any(CaseRelation.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateCaseRelationRequest req = new CreateCaseRelationRequest(relatedId, RelationType.DUPLICATE, "Same issue reported twice");
        CaseRelationResponse resp = service.linkCases(primaryId, req, operatorId);

        assertThat(resp).isNotNull();
        assertThat(resp.getRelationType()).isEqualTo(RelationType.DUPLICATE);
        assertThat(resp.getLinkedByName()).isEqualTo("Operator John");
        assertThat(resp.getNotes()).isEqualTo("Same issue reported twice");
        verify(caseRelationRepository, times(1)).save(any(CaseRelation.class));
    }

    @Test
    @DisplayName("linkCases — self-relation throws BadRequestException")
    void linkCases_selfRelation_throwsBadRequest() {
        CreateCaseRelationRequest req = new CreateCaseRelationRequest(primaryId, RelationType.RELATED, "Self link");

        assertThatThrownBy(() -> service.linkCases(primaryId, req, operatorId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot be related to itself");

        verify(caseRelationRepository, never()).save(any());
    }

    @Test
    @DisplayName("linkCases — cross-organization link throws BadRequestException")
    void linkCases_differentOrg_throwsBadRequest() {
        Organization otherOrg = new Organization("Other Org");
        otherOrg.setId(UUID.randomUUID());
        Category otherCat = new Category(otherOrg, "Other Cat", null, null);
        otherCat.setId(UUID.randomUUID());
        relatedCase.setCategory(otherCat);

        when(caseRepository.findById(primaryId)).thenReturn(Optional.of(primaryCase));
        when(caseRepository.findById(relatedId)).thenReturn(Optional.of(relatedCase));

        CreateCaseRelationRequest req = new CreateCaseRelationRequest(relatedId, RelationType.RELATED, "Cross org");

        assertThatThrownBy(() -> service.linkCases(primaryId, req, operatorId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("different organizations");

        verify(caseRelationRepository, never()).save(any());
    }

    @Test
    @DisplayName("linkCases — duplicate existing relation throws BadRequestException")
    void linkCases_alreadyExists_throwsBadRequest() {
        when(caseRepository.findById(primaryId)).thenReturn(Optional.of(primaryCase));
        when(caseRepository.findById(relatedId)).thenReturn(Optional.of(relatedCase));
        when(caseRelationRepository.existsByCaseEntityIdAndRelatedCaseEntityIdAndRelationType(primaryId, relatedId, RelationType.DUPLICATE))
                .thenReturn(true);

        CreateCaseRelationRequest req = new CreateCaseRelationRequest(relatedId, RelationType.DUPLICATE, "Duplicate attempt");

        assertThatThrownBy(() -> service.linkCases(primaryId, req, operatorId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");

        verify(caseRelationRepository, never()).save(any());
    }

    @Test
    @DisplayName("removeRelation — successfully deletes existing relation")
    void removeRelation_success() {
        UUID relationId = UUID.randomUUID();
        CaseRelation rel = new CaseRelation(primaryCase, relatedCase, RelationType.RELATED, operator, "Note");
        when(caseRelationRepository.findById(relationId)).thenReturn(Optional.of(rel));

        service.removeRelation(relationId);

        verify(caseRelationRepository, times(1)).delete(rel);
    }

    @Test
    @DisplayName("getMasterIncidentChildren — returns child cases")
    void getMasterIncidentChildren_success() {
        when(caseRepository.existsById(primaryId)).thenReturn(true);
        CaseRelation rel = new CaseRelation(primaryCase, relatedCase, RelationType.MASTER_INCIDENT, operator, "Master incident child");
        when(caseRelationRepository.findMasterIncidentChildren(primaryId)).thenReturn(List.of(rel));

        List<CaseRelationResponse> children = service.getMasterIncidentChildren(primaryId);

        assertThat(children).hasSize(1);
        assertThat(children.get(0).getRelationType()).isEqualTo(RelationType.MASTER_INCIDENT);
    }
}
