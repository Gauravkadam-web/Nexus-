package com.nexus.organization.repository;

import com.nexus.organization.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByOrganizationId(UUID organizationId);
    List<Team> findByLeadUserId(UUID leadUserId);
}
