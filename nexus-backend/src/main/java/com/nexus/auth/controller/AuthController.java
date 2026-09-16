package com.nexus.auth.controller;

import com.nexus.auth.dto.AuthResponse;
import com.nexus.auth.dto.LoginRequest;
import com.nexus.auth.dto.RegisterRequest;
import com.nexus.auth.dto.TokenRefreshRequest;
import com.nexus.auth.security.UserPrincipal;
import com.nexus.auth.service.AuthService;
import com.nexus.common.response.ApiResponse;
import com.nexus.user.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints (register, login, refresh, profile).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User authenticated successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new com.nexus.common.exception.UnauthorizedException("User is not authenticated");
        }
        UserDto userDto = authService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success(userDto, "User profile retrieved successfully"));
    }
}
