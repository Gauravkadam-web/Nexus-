package com.nexus.auth.service;

import com.nexus.auth.dto.AuthResponse;
import com.nexus.auth.dto.LoginRequest;
import com.nexus.auth.dto.RegisterRequest;
import com.nexus.auth.dto.TokenRefreshRequest;
import com.nexus.auth.security.JwtProvider;
import com.nexus.auth.security.UserPrincipal;
import com.nexus.common.exception.BadRequestException;
import com.nexus.common.exception.ResourceNotFoundException;
import com.nexus.common.exception.UnauthorizedException;
import com.nexus.config.JwtConfig;
import com.nexus.organization.entity.Organization;
import com.nexus.organization.repository.OrganizationRepository;
import com.nexus.user.dto.UserDto;
import com.nexus.user.entity.Role;
import com.nexus.user.entity.RoleType;
import com.nexus.user.entity.User;
import com.nexus.user.repository.RoleRepository;
import com.nexus.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JwtConfig jwtConfig;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       OrganizationRepository organizationRepository,
                       PasswordEncoder passwordEncoder,
                       JwtProvider jwtProvider,
                       JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.jwtConfig = jwtConfig;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        String orgName = request.getOrganizationName() != null && !request.getOrganizationName().isBlank()
                ? request.getOrganizationName()
                : "Default Organization";

        Organization organization = organizationRepository.findAll().stream()
                .filter(o -> o.getName().equalsIgnoreCase(orgName))
                .findFirst()
                .orElseGet(() -> organizationRepository.save(new Organization(orgName)));

        RoleType roleType = request.getRole() != null ? request.getRole() : RoleType.REQUESTER;
        Role role = roleRepository.findByName(roleType)
                .orElseGet(() -> roleRepository.save(new Role(roleType)));

        User user = new User(organization, request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton(role));
        User savedUser = userRepository.save(user);

        UserPrincipal principal = UserPrincipal.create(savedUser);
        String accessToken = jwtProvider.generateAccessToken(principal);
        String refreshToken = jwtProvider.generateRefreshToken(principal);

        return new AuthResponse(accessToken, refreshToken, jwtConfig.getAccessTokenExpirationMs() / 1000, UserDto.fromEntity(savedUser));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtProvider.generateAccessToken(principal);
        String refreshToken = jwtProvider.generateRefreshToken(principal);

        return new AuthResponse(accessToken, refreshToken, jwtConfig.getAccessTokenExpirationMs() / 1000, UserDto.fromEntity(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String token = request.getRefreshToken();
        if (!jwtProvider.validateToken(token)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        UUID userId = jwtProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserPrincipal principal = UserPrincipal.create(user);
        String newAccessToken = jwtProvider.generateAccessToken(principal);

        return new AuthResponse(newAccessToken, token, jwtConfig.getAccessTokenExpirationMs() / 1000, UserDto.fromEntity(user));
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserDto.fromEntity(user);
    }
}
