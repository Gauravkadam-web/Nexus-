package com.nexus.organization.controller;

import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.response.ApiResponse;
import com.nexus.organization.dto.CategoryDto;
import com.nexus.organization.dto.CreateCategoryRequest;
import com.nexus.organization.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories(@AuthenticationPrincipal UserPrincipal principal) {
        List<CategoryDto> categories = categoryService.getCategories(principal);
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        CategoryDto category = categoryService.createCategory(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(category, "Category created successfully"));
    }
}
