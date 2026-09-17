package com.nexus.casemanagement.service;

import com.nexus.casemanagement.dto.CaseSearchRequest;
import com.nexus.casemanagement.dto.CaseSummaryResponse;
import com.nexus.casemanagement.entity.Case;
import com.nexus.casemanagement.entity.CaseStatus;
import com.nexus.casemanagement.entity.Priority;
import com.nexus.casemanagement.entity.Severity;
import com.nexus.casemanagement.repository.CaseAssignmentRepository;
import com.nexus.casemanagement.repository.CaseRepository;
import com.nexus.casemanagement.statemachine.CaseLifecycleService;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.sla.repository.CaseRiskRepository;
import com.nexus.sla.repository.CaseSlaRepository;
import com.nexus.sla.repository.SlaPolicyRepository;
import com.nexus.sla.service.CaseRiskService;
import com.nexus.sla.service.SlaService;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CaseSearch — US-34 Unit Tests")
class CaseSearchTest {

    @Mock private CaseRepository caseRepository;
    @Mock private CaseAssignmentRepository caseAssignmentRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private SlaPolicyRepository slaPolicyRepository;
    @Mock private CaseSlaRepository caseSlaRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private CaseRiskRepository caseRiskRepository;

    private CaseService caseService;

    private Organization org;
    private Category category;
    private User requester;
    private Case sampleCase;

    @BeforeEach
    void setUp() {
        CaseLifecycleService lifecycleService = new CaseLifecycleService();
        CaseRiskService caseRiskService = new CaseRiskService(caseRiskRepository);
        SlaService slaService = new SlaService(
                slaPolicyRepository,
                caseSlaRepository,
                organizationRepository,
                categoryRepository,
                caseRiskService
        );

        caseService = new CaseService(
                caseRepository,
                caseAssignmentRepository,
                categoryRepository,
                teamRepository,
                userRepository,
                lifecycleService,
                eventPublisher,
                slaService
        );

        org = new Organization("Acme Global");
        org.setId(UUID.randomUUID());

        category = new Category(org, "Infrastructure", null, null);
        category.setId(UUID.randomUUID());

        requester = new User(org, "Bob", "bob@acme.com", "hash");
        requester.setId(UUID.randomUUID());

        sampleCase = new Case();
        sampleCase.setId(UUID.randomUUID());
        sampleCase.setCaseNumber("NEX-20260917-0001");
        sampleCase.setTitle("VPN Gateway Down");
        sampleCase.setDescription("Users cannot connect to US-East VPN tunnel");
        sampleCase.setCategory(category);
        sampleCase.setPriority(Priority.HIGH);
        sampleCase.setSeverity(Severity.HIGH);
        sampleCase.setStatus(CaseStatus.INVESTIGATING);
        sampleCase.setRequester(requester);
        sampleCase.setLocation("New York HQ");
    }

    @Test
    @DisplayName("US-34: Multi-criteria case search delegates to specification executor")
    @SuppressWarnings("unchecked")
    void searchCases_success() {
        CaseSearchRequest request = new CaseSearchRequest();
        request.setQuery("VPN");
        request.setStatus(CaseStatus.INVESTIGATING);
        request.setPriority(Priority.HIGH);
        request.setLocation("New York");

        Pageable pageable = PageRequest.of(0, 10);
        when(caseRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(sampleCase), pageable, 1));

        Page<CaseSummaryResponse> result = caseService.searchCases(request, org.getId(), pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("VPN Gateway Down");
    }

    @Test
    @DisplayName("US-34: Specification builder handles empty criteria safely")
    void specification_emptyFilters() {
        CaseSearchRequest request = new CaseSearchRequest();
        Specification<Case> spec = CaseSpecification.withFilters(request, null);
        assertThat(spec).isNotNull();
    }
}
