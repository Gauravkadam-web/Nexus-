package com.nexus.escalation.repository;

import com.nexus.escalation.entity.EscalationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EscalationRuleRepository extends JpaRepository<EscalationRule, UUID> {

    List<EscalationRule> findByOrganizationId(UUID organizationId);

    List<EscalationRule> findByOrganizationIdAndActiveTrue(UUID organizationId);
}
