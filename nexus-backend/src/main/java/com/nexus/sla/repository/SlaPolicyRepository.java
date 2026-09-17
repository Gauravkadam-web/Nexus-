package com.nexus.sla.repository;

import com.nexus.casemanagement.entity.Priority;
import com.nexus.sla.entity.SlaPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, UUID> {

    List<SlaPolicy> findByOrganizationId(UUID organizationId);

    Optional<SlaPolicy> findByOrganizationIdAndCategoryIdAndPriority(UUID organizationId, UUID categoryId, Priority priority);

    Optional<SlaPolicy> findByOrganizationIdAndCategoryIdIsNullAndPriority(UUID organizationId, Priority priority);
}
