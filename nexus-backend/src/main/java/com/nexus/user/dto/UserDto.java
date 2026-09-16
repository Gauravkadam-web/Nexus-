package com.nexus.user.dto;

import com.nexus.user.entity.Role;
import com.nexus.user.entity.User;
import com.nexus.user.entity.UserStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserDto {

    private UUID id;
    private UUID organizationId;
    private String organizationName;
    private String name;
    private String email;
    private UserStatus status;
    private Set<String> roles;
    private Instant createdAt;

    public UserDto() {}

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        if (user.getOrganization() != null) {
            dto.setOrganizationId(user.getOrganization().getId());
            dto.setOrganizationName(user.getOrganization().getName());
        }
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setRoles(user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet()));
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
