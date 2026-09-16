package com.nexus.auth.security;

import com.nexus.user.entity.User;
import com.nexus.user.entity.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final UUID organizationId;
    private final String name;
    private final String email;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UUID id, UUID organizationId, String name, String email, String password,
                         boolean active, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toSet());

        return new UserPrincipal(
                user.getId(),
                user.getOrganization() != null ? user.getOrganization().getId() : null,
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getStatus() == UserStatus.ACTIVE,
                authorities
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
