package com.nexus.organization.repository;

import com.nexus.organization.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByOrganizationId(UUID organizationId);
    List<Category> findByOrganizationIdAndParentCategoryIsNull(UUID organizationId);
    List<Category> findByParentCategoryId(UUID parentCategoryId);
}
