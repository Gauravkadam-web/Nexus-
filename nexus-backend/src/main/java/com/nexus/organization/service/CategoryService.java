package com.nexus.organization.service;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.organization.dto.CategoryDto;
import com.nexus.organization.dto.CreateCategoryRequest;
import com.nexus.organization.entity.Category;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.entity.Team;
import com.nexus.organization.repository.CategoryRepository;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.organization.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final OrganizationRepository organizationRepository;
    private final TeamRepository teamRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           OrganizationRepository organizationRepository,
                           TeamRepository teamRepository) {
        this.categoryRepository = categoryRepository;
        this.organizationRepository = organizationRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(UserPrincipal principal) {
        UUID orgId = principal.getOrganizationId();
        if (orgId == null) {
            return categoryRepository.findAll().stream()
                    .map(CategoryDto::fromEntity)
                    .collect(Collectors.toList());
        }
        return categoryRepository.findByOrganizationId(orgId).stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request, UserPrincipal principal) {
        UUID orgId = principal.getOrganizationId();
        if (orgId == null) {
            throw new BadRequestException("User must belong to an organization to create categories");
        }

        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Category parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
        }

        Team defaultTeam = null;
        if (request.getDefaultTeamId() != null) {
            defaultTeam = teamRepository.findById(request.getDefaultTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Default team not found"));
        }

        Category category = new Category(organization, request.getName(), parentCategory, defaultTeam);
        Category saved = categoryRepository.save(category);
        return CategoryDto.fromEntity(saved);
    }
}
