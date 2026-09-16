package com.nexus.organization.service;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.organization.dto.CreateTeamRequest;
import com.nexus.organization.dto.TeamDto;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.organization.repository.TeamRepository;
import com.nexus.user.entity.User;
import com.nexus.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository,
                       OrganizationRepository organizationRepository,
                       UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getTeams(UserPrincipal principal) {
        UUID orgId = principal.getOrganizationId();
        if (orgId == null) {
            return teamRepository.findAll().stream()
                    .map(TeamDto::fromEntity)
                    .collect(Collectors.toList());
        }
        return teamRepository.findByOrganizationId(orgId).stream()
                .map(TeamDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeamDto createTeam(CreateTeamRequest request, UserPrincipal principal) {
        UUID orgId = principal.getOrganizationId();
        if (orgId == null) {
            throw new BadRequestException("User must belong to an organization to create teams");
        }

        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        User leadUser = null;
        if (request.getLeadUserId() != null) {
            leadUser = userRepository.findById(request.getLeadUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lead user not found"));
        }

        Team team = new Team(organization, request.getName(), leadUser);
        Team saved = teamRepository.save(team);
        return TeamDto.fromEntity(saved);
    }
}
